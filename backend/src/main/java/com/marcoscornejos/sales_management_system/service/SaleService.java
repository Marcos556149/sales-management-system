package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.exception.*;
import com.marcoscornejos.sales_management_system.mapper.IPageResponseMapper;
import com.marcoscornejos.sales_management_system.mapper.ISaleListResponseMapper;
import com.marcoscornejos.sales_management_system.mapper.ISaleWithDetailsResponseMapper;
import com.marcoscornejos.sales_management_system.model.*;
import com.marcoscornejos.sales_management_system.repository.IProductRepository;
import com.marcoscornejos.sales_management_system.repository.ISaleRepository;
import com.marcoscornejos.sales_management_system.repository.ISystemConfigurationRepository;
import com.marcoscornejos.sales_management_system.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SaleService implements ISaleService {

        private final ISaleRepository iSaleRepository;
        private final IPageResponseMapper iPageResponseMapper;
        private final ISaleListResponseMapper iSaleListResponseMapper;
        private final ISaleWithDetailsResponseMapper iSaleWithDetailsResponseMapper;
        private final IUserRepository iUserRepository;
        private final IProductRepository iProductRepository;
        private final ISystemConfigurationRepository iSystemConfigurationRepository;

        /**
         * Retrieves a paginated list of sales applying:
         * <ul>
         * <li>Optional search by sale identifier</li>
         * <li>Filter by sale date</li>
         * <li>Chronological sorting by sale time</li>
         * <li>Pagination (page number and size)</li>
         * </ul>
         *
         * <p>
         * Search by sale identifier is optional and ignored if null.
         * If no date is provided, the current date is used by default.
         * Pagination and sorting are executed at database level.
         * </p>
         *
         * @param searchSaleId Optional sale identifier
         * @param date         Sale date filter (if null, current date is used)
         * @param timeSort     Sorting direction (NEWEST_FIRST / OLDEST_FIRST)
         * @param page         Page number (0-based)
         * @param size         Number of elements per page
         * @return Paginated list of sales mapped to DTO
         */
        @Override
        public PageResponseDTO<SaleListResponseDTO> getSales(Long searchSaleId,
                        LocalDate date,
                        SortDirection timeSort,
                        int page,
                        int size) {

                // Validate pagination parameters
                if (page < 0) {
                        throw new InvalidSaleDataException(
                                        "Page index must not be negative",
                                        "page");
                }

                if (size <= 0) {
                        throw new InvalidSaleDataException(
                                        "Page size must be greater than zero",
                                        "size");
                }

                if (size > 50) {
                        throw new InvalidSaleDataException(
                                        "Page size must not exceed 50",
                                        "size");
                }

                // Validate optional search parameter
                if (searchSaleId != null && searchSaleId <= 0) {
                        throw new InvalidSaleDataException(
                                        "Sale ID must be greater than zero",
                                        "searchSaleId");
                }

                // Business rule: default date = current date
                if (date == null) {
                        date = LocalDate.now();
                }

                // Build sorting configuration (by sale time)
                Sort sort = Sort.by("saleTime");

                if (timeSort == SortDirection.NEWEST_FIRST) {
                        sort = sort.descending();
                } else {
                        sort = sort.ascending();
                }

                // Pagination configuration (server-side)
                Pageable pageable = PageRequest.of(page, size, sort);

                // Execute query with search, filtering and pageable
                Page<Sale> salePage = iSaleRepository.findSales(
                                searchSaleId,
                                date,
                                pageable);

                // Total number of sales in database without filters.
                // This value is only calculated when the filtered query returns no results,
                // allowing the frontend to distinguish between:
                //
                // 1) No sales exist in the database
                // 2) Sales exist, but none match the selected date filter
                //
                // When filtered results exist, this value remains null to avoid an
                // unnecessary extra COUNT(*) query and improve performance.
                Long totalGlobalElements = null;

                if (salePage.getTotalElements() == 0) {
                        totalGlobalElements = iSaleRepository.count();
                }

                // Map entities to DTOs using MapStruct
                return iPageResponseMapper.toPageResponseDTO(
                                salePage.getContent()
                                                .stream()
                                                .map(iSaleListResponseMapper::toDto)
                                                .toList(),
                                salePage.getNumber(),
                                salePage.getSize(),
                                salePage.getTotalPages(),
                                salePage.getTotalElements(),
                                totalGlobalElements);
        }

        /**
         * Retrieves a sale by its unique identifier.
         *
         * <p>
         * Searches for a sale in the database including its details and products.
         * Throws an exception if the sale does not exist.
         * </p>
         *
         * @param saleId the unique identifier of the sale
         * @return the sale details as a DTO
         * @throws SaleNotFoundException if the sale is not found
         */
        @Override
        @Transactional
        public SaleWithDetailsResponseDTO getSaleById(Long saleId) {

                Sale sale = iSaleRepository.findByIdWithDetailsAndProducts(saleId)
                                .orElseThrow(() -> new SaleNotFoundException(
                                                String.format("Sale with ID '%s' not found", saleId)));

                return iSaleWithDetailsResponseMapper.toDto(sale);
        }

        /**
         * Builds and returns available sorting options for sales.
         *
         * <p>
         * This method extracts values from {@link SortOrder} enum
         * and converts them into a frontend-friendly {@link EnumDTO} format.
         * </p>
         *
         * <p>
         * It ensures that the frontend always receives up-to-date options
         * without requiring code changes on the client side.
         * </p>
         *
         * @return SaleFiltersResponseDTO containing sort options
         */
        @Override
        public SaleFiltersResponseDTO getFilters() {

                List<EnumDTO> timeSortOptions = Arrays.stream(SortDirection.values())
                                .map(sort -> new EnumDTO(
                                                sort.name(),
                                                sort.getDisplayName()))
                                .toList();

                return new SaleFiltersResponseDTO(timeSortOptions);
        }

        /**
         * Registers a new sale in the system.
         *
         * <p>
         * Validates that all requested products exist, are active, and have sufficient
         * stock before creating the sale. If the same product appears multiple times
         * in the request, quantities are consolidated into a single line item.
         * </p>
         *
         * <p>
         * Each sale is associated with the user performing the operation, and the
         * total sale amount is calculated based on product prices at the time of sale.
         * </p>
         *
         * <p>
         * The operation is executed within a transactional context to ensure atomic
         * persistence of the sale and its details.
         * </p>
         *
         * <p>
         * After successful persistence, the unique identifier of the created sale is returned.
         * This identifier can be used to retrieve related information such as the printable ticket.
         * </p>
         *
         * @param request the sale creation request containing products and quantities
         * @return the unique identifier of the created sale
         *
         * @throws UserNotFoundException    if the user performing the sale does not exist
         * @throws ProductNotFoundException if any product does not exist
         * @throws InvalidSaleDataException if a product is inactive, has invalid quantity,
         *                                  or insufficient stock is available
         */
        @Override
        @Transactional
        public Long registerSale(SaleCreateRequestDTO request) {

                // Temporary default user until Spring Security is implemented
                User user = iUserRepository.findById(2L)
                                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

                /*
                 * // Future implementation with authenticated user
                 * String username = SecurityContextHolder.getContext()
                 * .getAuthentication()
                 * .getName();
                 * 
                 * User user = iUserRepository.findByUserName(username)
                 * .orElseThrow(() ->
                 * new UserNotFoundException("Authenticated user not found")
                 * );
                 */

                // Consolidate repeated product codes into a single line item
                Map<String, BigDecimal> groupedDetails = new LinkedHashMap<>();

                for (SaleDetailCreateRequestDTO detail : request.getSaleDetails()) {
                        groupedDetails.merge(
                                        detail.getProductCode(),
                                        detail.getProductQuantity(),
                                        BigDecimal::add);
                }

                // Build normalized and validated sale details list
                List<SaleDetail> saleDetails = new ArrayList<>();

                // Load all requested products in a single query
                List<String> productCodes = new ArrayList<>(groupedDetails.keySet());

                Map<String, Product> productsByCode = iProductRepository
                                .findAllById(productCodes)
                                .stream()
                                .collect(Collectors.toMap(
                                                Product::getProductCode,
                                                Function.identity()));

                // Validate each requested product and build normalized sale details
                for (Map.Entry<String, BigDecimal> entry : groupedDetails.entrySet()) {

                        String productCode = entry.getKey();
                        BigDecimal quantity = entry.getValue();

                        Product product = productsByCode.get(productCode);

                        if (product == null) {
                                throw new ProductNotFoundException(
                                                "Product with code '" + productCode + "' not found");
                        }

                        String productLabel = product.getProductCode()
                                        + " - "
                                        + product.getProductName();

                        // Only active products can be sold
                        if (product.getProductStatus() != ProductStatus.ACTIVE) {
                                throw new InvalidSaleDataException(
                                                "Product '" + productLabel
                                                                + "' is inactive and cannot be added to the sale",
                                                "productCode");
                        }

                        // Products sold by units do not allow decimal quantities
                        if (product.getUnitOfMeasure() == UnitOfMeasure.UNITS
                                        && quantity.stripTrailingZeros().scale() > 0) {

                                throw new InvalidSaleDataException(
                                                "Product '" + productLabel
                                                                + "' only accepts whole numbers because it is sold by units",
                                                "productQuantity");
                        }

                        // Requested quantity must not exceed available stock
                        if (product.getProductStock().compareTo(quantity) < 0) {
                                throw new InvalidSaleDataException(
                                                "Insufficient stock for product " + productLabel,
                                                "productQuantity");
                        }

                        // Create sale detail using current product price and unit of measure snapshot
                        SaleDetail saleDetail = new SaleDetail();
                        saleDetail.setProduct(product);
                        saleDetail.setProductQuantity(quantity);
                        saleDetail.setSalePrice(product.getProductPrice());
                        saleDetail.setUnitOfMeasureAtSale(product.getUnitOfMeasure());
                        saleDetail.setProductNameAtSale(product.getProductName());

                        saleDetails.add(saleDetail);
                }

                // Create sale entity and associate user
                Sale sale = new Sale();
                sale.setUser(user);

                // Synchronize bidirectional relationship between Sale and SaleDetail
                for (SaleDetail detail : saleDetails) {
                        detail.setSale(sale);
                        sale.getSaleDetails().add(detail);
                }

                // Persist sale and sale details (handled via CascadeType.PERSIST)
                // Stock updates and final total recalculation are managed by database triggers,
                // ensuring consistency at persistence level.
                // The generated sale ID is returned after persistence.
                Sale savedSale = iSaleRepository.save(sale);
                return savedSale.getSaleId();
        }

        /**
         * Generates a formatted sale ticket for a given sale ID.
         *
         * <p>
         * Retrieves the sale along with its details and products, as well as
         * the system configuration containing business information.
         * Then builds a plain text ticket formatted for thermal printing.
         * </p>
         *
         * @param saleId the unique identifier of the sale
         * @return the formatted sale ticket as plain text
         * @throws SaleNotFoundException if the sale is not found
         */
        @Override
        @Transactional
        public String generateSaleTicket(Long saleId) {

                // 1. Retrieve sale with details and products (1 query bien optimizada)
                Sale sale = iSaleRepository.findByIdWithDetails(saleId)
                                .orElseThrow(() -> new SaleNotFoundException(
                                                String.format("Sale with ID '%s' not found", saleId)));

                // 2. Retrieve system configuration (business info)
                SystemConfiguration config = iSystemConfigurationRepository.findById(1L)
                        .orElseThrow(() -> new SystemConfigurationNotFoundException(
                                "System configuration not found"
                        ));

                // 3. Current date and time
                LocalDateTime now = LocalDateTime.now();

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                // 4. Build ticket
                StringBuilder ticket = new StringBuilder();

                ticket.append(" \n");

                // Business name (wrapped, left-aligned)
                for (String line : wrapText(config.getBusinessName(), 32)) {
                        ticket.append(line).append("\n");
                }

                ticket.append("--------------------------------\n");

                // Business address (wrapped, left-aligned)
                for (String line : wrapText(config.getBusinessAddress(), 32)) {
                        ticket.append(line).append("\n");
                }
                ticket.append("--------------------------------\n");

                ticket.append("Date: ").append(now.format(dateFormatter)).append("\n");
                ticket.append("Time: ").append(now.format(timeFormatter)).append("\n");
                ticket.append("--------------------------------\n");

                // Sale id
                ticket.append("Sale ID: ").append(sale.getSaleId()).append("\n");
                ticket.append("--------------------------------\n");

                // Products header
                ticket.append("PRODUCTS\n");
                ticket.append("--------------------------------\n");
                ticket.append(" \n");

                // --- Details ---
                for (SaleDetail detail : sale.getSaleDetails()) {

                        String productName = detail.getProductNameAtSale();
                        String quantity = detail.getProductQuantity().stripTrailingZeros().toPlainString();
                        String unit = detail.getUnitOfMeasureAtSale().getAbbreviation();

                        String unitPrice = "$" + detail.getSalePrice().setScale(2, RoundingMode.HALF_UP);
                        BigDecimal subtotal = detail.getSalePrice()
                                .multiply(detail.getProductQuantity())
                                .setScale(2, RoundingMode.HALF_UP);

                        String subtotalFormatted = "$" + subtotal;

                        // Wrap product name if it exceeds ticket width
                        List<String> nameLines = wrapText(productName, 32);

                        for (String line : nameLines) {
                                ticket.append(line).append("\n");
                        }

                        // Build main item line (quantity, unit, unit price, subtotal)
                        String itemLine = String.format(
                                "%s %s x %s = %s",
                                quantity,
                                unit,
                                unitPrice,
                                subtotalFormatted
                        );

                        // Wrap full item line to avoid overflow in thermal printer
                        List<String> itemLines = wrapText(itemLine, 32);

                        for (String line : itemLines) {
                                ticket.append(line).append("\n");
                        }

                        // Spacer between products
                        ticket.append(" \n");

                }

                ticket.append("--------------------------------\n");

                // --- Total ---
                ticket.append(String.format(
                        "TOTAL: $%s\n",
                        sale.getTotalAmount().toPlainString()
                ));

                ticket.append(" \n");
                ticket.append(center("Thank you for your purchase!")).append("\n");

                for (int i = 0; i < 4; i++) {
                        ticket.append(" \n");
                }

                ticket.append(" \n");

                return ticket.toString();
        }

        /**
         * Centers text for ticket printing assuming fixed width.
         *
         * @param text the text to center
         * @return centered text padded with spaces
         */
        private String center(String text) {

                final int LINE_WIDTH = 32;

                if (text == null) {
                        return "";
                }

                if (text.length() >= LINE_WIDTH) {
                        return text;
                }

                int padding = (LINE_WIDTH - text.length()) / 2;

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < padding; i++) {
                        sb.append(" ");
                }

                sb.append(text);

                return sb.toString();
        }

        /**
         * Wraps a text into multiple lines without breaking words whenever possible.
         *
         * <p>
         * This method formats text to fit within a fixed-width layout (such as a thermal printer),
         * ensuring that words are preserved and not split across lines unless a single word exceeds
         * the maximum width. In such cases, the word is split as a fallback.
         * </p>
         *
         * <p>
         * It also normalizes whitespace by collapsing multiple spaces into a single space.
         * </p>
         *
         * @param text the text to be wrapped
         * @param width the maximum number of characters per line
         * @return a list of properly wrapped lines that fit within the specified width
         */
        private List<String> wrapText(String text, int width) {
                List<String> lines = new ArrayList<>();

                if (text == null || text.isBlank()) {
                        return lines;
                }

                String[] words = text.trim().split("\\s+");
                StringBuilder currentLine = new StringBuilder();

                for (String word : words) {

                        // If the word itself is longer than the width, split it forcibly
                        if (word.length() > width) {

                                if (currentLine.length() > 0) {
                                        lines.add(currentLine.toString());
                                        currentLine.setLength(0);
                                }

                                for (int i = 0; i < word.length(); i += width) {
                                        lines.add(word.substring(i, Math.min(i + width, word.length())));
                                }

                                continue;
                        }

                        // If the word fits in the current line
                        if (currentLine.length() == 0) {
                                currentLine.append(word);
                        } else if (currentLine.length() + 1 + word.length() <= width) {
                                currentLine.append(" ").append(word);
                        } else {
                                // Move to next line
                                lines.add(currentLine.toString());
                                currentLine.setLength(0);
                                currentLine.append(word);
                        }
                }

                // Add remaining content
                if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                }

                return lines;
        }

}
