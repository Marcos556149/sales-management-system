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
         * Recupera una lista paginada de ventas aplicando:
         * <ul>
         * <li>Búsqueda opcional por identificador de venta</li>
         * <li>Filtro por fecha de venta</li>
         * <li>Ordenamiento cronológico por hora de venta</li>
         * <li>Paginación (número de página y tamaño)</li>
         * </ul>
         *
         * <p>
         * La búsqueda por identificador de venta es opcional y se ignora si es null.
         * Si no se proporciona una fecha, se utiliza la fecha actual por defecto.
         * La paginación y el ordenamiento se ejecutan a nivel de base de datos.
         * </p>
         *
         * @param searchSaleId identificador de venta opcional
         * @param date fecha de venta a filtrar (si es null, se utiliza la fecha actual)
         * @param timeSort dirección del ordenamiento (NEWEST_FIRST / OLDEST_FIRST)
         * @param page número de página (basado en 0)
         * @param size cantidad de elementos por página
         * @return lista paginada de ventas mapeadas a DTO
         */
        @Override
        public PageResponseDTO<SaleListResponseDTO> getSales(Long searchSaleId,
                        LocalDate date,
                        SortDirection timeSort,
                        int page,
                        int size) {

                // Validar parámetros de paginación
                if (page < 0) {
                        throw new InvalidSaleDataException(
                                        "El índice de página no puede ser negativo",
                                        "page");
                }

                if (size <= 0) {
                        throw new InvalidSaleDataException(
                                        "El tamaño de página debe ser mayor que cero",
                                        "size");
                }

                if (size > 50) {
                        throw new InvalidSaleDataException(
                                        "El tamaño de página no puede ser mayor a 50",
                                        "size");
                }

                // Validar parámetro de búsqueda opcional
                if (searchSaleId != null && searchSaleId <= 0) {
                        throw new InvalidSaleDataException(
                                        "El identificador de venta debe ser mayor que cero",
                                        "searchSaleId");
                }

                // Regla de negocio: fecha por defecto = fecha actual
                if (date == null) {
                        date = LocalDate.now();
                }

                // Construir configuración de ordenamiento (por hora de venta)
                Sort sort = Sort.by("saleTime");

                if (timeSort == SortDirection.NEWEST_FIRST) {
                        sort = sort.descending();
                } else {
                        sort = sort.ascending();
                }

                // Configuración de paginación (del lado del servidor)
                Pageable pageable = PageRequest.of(page, size, sort);

                // Ejecutar consulta con búsqueda, filtrado y paginación
                Page<Sale> salePage = iSaleRepository.findSales(
                                searchSaleId,
                                date,
                                pageable);

                // Cantidad total de ventas en la base de datos sin aplicar filtros.
                //
                // Este valor solo se calcula cuando la consulta filtrada no devuelve resultados,
                // permitiendo al frontend distinguir entre:
                //
                // 1) No existen ventas en la base de datos
                // 2) Existen ventas, pero ninguna coincide con el filtro de fecha seleccionado
                //
                // Cuando existen resultados filtrados, este valor permanece en null para evitar
                // una consulta COUNT(*) adicional innecesaria y mejorar el rendimiento.
                Long totalGlobalElements = null;

                if (salePage.getTotalElements() == 0) {
                        totalGlobalElements = iSaleRepository.count();
                }

                // Mapear entidades a DTOs utilizando MapStruct
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
         * Recupera una venta mediante su identificador único.
         *
         * <p>
         * Busca una venta en la base de datos junto con sus detalles y productos.
         * Lanza una excepción si la venta no existe.
         * </p>
         *
         * @param saleId identificador único de la venta
         * @return los detalles de la venta como un DTO
         * @throws SaleNotFoundException si la venta no es encontrada
         */
        @Override
        @Transactional
        public SaleWithDetailsResponseDTO getSaleById(Long saleId) {

                Sale sale = iSaleRepository.findByIdWithDetailsAndProducts(saleId)
                                .orElseThrow(() -> new SaleNotFoundException(
                                                String.format("No se encontró la venta con identificador '%s'", saleId)));

                return iSaleWithDetailsResponseMapper.toDto(sale);
        }

        /**
         * Construye y devuelve las opciones de ordenamiento disponibles para las ventas.
         *
         * <p>
         * Este método obtiene los valores del enum {@link SortDirection}
         * y los convierte a un formato {@link EnumDTO} compatible con el frontend.
         * </p>
         *
         * <p>
         * Garantiza que el frontend siempre reciba opciones actualizadas
         * sin requerir cambios de código en el cliente.
         * </p>
         *
         * @return SaleFiltersResponseDTO que contiene las opciones de ordenamiento
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
         * Registra una nueva venta en el sistema.
         *
         * <p>
         * Valida que todos los productos solicitados existan, estén activos y tengan
         * stock suficiente antes de crear la venta. Si el mismo producto aparece varias veces
         * en la solicitud, las cantidades se consolidan en una única línea de detalle.
         * </p>
         *
         * <p>
         * Cada venta se asocia al usuario que realiza la operación y el importe total
         * de la venta se calcula en función de los precios de los productos al momento de la venta.
         * </p>
         *
         * <p>
         * La operación se ejecuta dentro de un contexto transaccional para garantizar
         * la persistencia atómica de la venta y sus detalles.
         * </p>
         *
         * <p>
         * Tras una persistencia exitosa, se devuelve el identificador único de la venta creada.
         * Este identificador puede utilizarse para recuperar información relacionada,
         * como el comprobante imprimible.
         * </p>
         *
         * @param request solicitud de creación de venta que contiene productos y cantidades
         * @return identificador único de la venta creada
         *
         * @throws UserNotFoundException si el usuario que realiza la venta no existe
         * @throws ProductNotFoundException si algún producto no existe
         * @throws InvalidSaleDataException si un producto está inactivo, tiene una cantidad inválida
         *                                  o no dispone de stock suficiente
         */
        @Override
        @Transactional
        public Long registerSale(SaleCreateRequestDTO request) {

                // Usuario temporal por defecto hasta que se implemente Spring Security
                User user = iUserRepository.findById(2L)
                                .orElseThrow(() -> new UserNotFoundException("Usuario autenticado no encontrado"));

                /*
                 * // Implementación futura con usuario autenticado
                 *
                 * ...
                 *
                 * new UserNotFoundException("Usuario autenticado no encontrado")
                 */

                // Consolidar códigos de producto repetidos en una única línea de detalle
                Map<String, BigDecimal> groupedDetails = new LinkedHashMap<>();

                for (SaleDetailCreateRequestDTO detail : request.getSaleDetails()) {
                        groupedDetails.merge(
                                        detail.getProductCode(),
                                        detail.getProductQuantity(),
                                        BigDecimal::add);
                }

                // Construir la lista de detalles de venta normalizada y validada
                List<SaleDetail> saleDetails = new ArrayList<>();

                // Cargar todos los productos solicitados en una única consulta
                List<String> productCodes = new ArrayList<>(groupedDetails.keySet());

                Map<String, Product> productsByCode = iProductRepository
                                .findAllById(productCodes)
                                .stream()
                                .collect(Collectors.toMap(
                                                Product::getProductCode,
                                                Function.identity()));

                // Validar cada producto solicitado y construir los detalles de venta normalizados
                for (Map.Entry<String, BigDecimal> entry : groupedDetails.entrySet()) {

                        String productCode = entry.getKey();
                        BigDecimal quantity = entry.getValue();

                        Product product = productsByCode.get(productCode);

                        if (product == null) {
                                throw new ProductNotFoundException(
                                        "No se encontró el producto con código '" + productCode + "'");
                        }

                        String productLabel = product.getProductCode()
                                        + " - "
                                        + product.getProductName();

                        // Solo los productos activos pueden ser vendidos
                        if (product.getProductStatus() != ProductStatus.ACTIVE) {
                                throw new InvalidSaleDataException(
                                                "El producto '" + productLabel
                                                                + "' está inactivo y no puede ser agregado a la venta",
                                                "productCode");
                        }

                        // Los productos vendidos por unidades no permiten cantidades decimales
                        if (product.getUnitOfMeasure() == UnitOfMeasure.UNITS
                                        && quantity.stripTrailingZeros().scale() > 0) {

                                throw new InvalidSaleDataException(
                                                "El producto '" + productLabel
                                                                + "' solo acepta números enteros porque se vende por unidades",
                                                "productQuantity");
                        }

                        // La cantidad solicitada no debe superar el stock disponible
                        if (product.getProductStock().compareTo(quantity) < 0) {
                                throw new InvalidSaleDataException(
                                                "Stock insuficiente para el producto " + productLabel,
                                                "productQuantity");
                        }

                        // Crear detalle de venta utilizando la instantánea actual del precio y la unidad de medida del producto
                        SaleDetail saleDetail = new SaleDetail();
                        saleDetail.setProduct(product);
                        saleDetail.setProductQuantity(quantity);
                        saleDetail.setSalePrice(product.getProductPrice());
                        saleDetail.setUnitOfMeasureAtSale(product.getUnitOfMeasure());
                        saleDetail.setProductNameAtSale(product.getProductName());

                        saleDetails.add(saleDetail);
                }

                // Crear entidad de venta y asociar usuario
                Sale sale = new Sale();
                sale.setUser(user);

                // Sincronizar la relación bidireccional entre Sale y SaleDetail
                for (SaleDetail detail : saleDetails) {
                        detail.setSale(sale);
                        sale.getSaleDetails().add(detail);
                }

                // Persistir la venta y sus detalles (gestionado mediante CascadeType.PERSIST)
                // Las actualizaciones de stock y el recálculo del importe total final son gestionados
                // por disparadores de base de datos, garantizando la consistencia a nivel de persistencia.
                // El identificador de la venta generado se devuelve después de la persistencia.
                Sale savedSale = iSaleRepository.save(sale);
                return savedSale.getSaleId();
        }

        /**
         * Genera un comprobante de venta formateado para un identificador de venta dado.
         *
         * <p>
         * Recupera la venta junto con sus detalles, así como la configuración
         * del sistema que contiene la información del negocio.
         * Luego construye un comprobante en texto plano con formato para impresión térmica.
         * </p>
         *
         * @param saleId identificador único de la venta
         * @return comprobante de venta formateado como texto plano
         * @throws SaleNotFoundException si la venta no es encontrada
         */
        @Override
        @Transactional
        public String generateSaleTicket(Long saleId) {

                // 1. Recuperar venta con sus detalles (consulta optimizada)
                Sale sale = iSaleRepository.findByIdWithDetails(saleId)
                                .orElseThrow(() -> new SaleNotFoundException(
                                                String.format("No se encontró la venta con identificador '%s'", saleId)));

                // 2. Recuperar configuración del sistema (información del negocio)
                SystemConfiguration config = iSystemConfigurationRepository.findById(1L)
                        .orElseThrow(() -> new SystemConfigurationNotFoundException(
                                "Configuración del sistema no encontrada"
                        ));

                // 3. Fecha y hora actuales
                LocalDateTime now = LocalDateTime.now();

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                // 4. Construir comprobante
                StringBuilder ticket = new StringBuilder();

                ticket.append(" \n");

                // Nombre del negocio (ajustado a varias líneas, alineado a la izquierda)
                for (String line : wrapText(config.getBusinessName(), 32)) {
                        ticket.append(line).append("\n");
                }

                ticket.append("--------------------------------\n");

                // Dirección del negocio (ajustada a varias líneas, alineada a la izquierda)
                for (String line : wrapText(config.getBusinessAddress(), 32)) {
                        ticket.append(line).append("\n");
                }
                ticket.append("--------------------------------\n");

                ticket.append("Fecha: ").append(now.format(dateFormatter)).append("\n");
                ticket.append("Hora: ").append(now.format(timeFormatter)).append("\n");
                ticket.append("--------------------------------\n");

                // Identificador de venta
                ticket.append("ID de Venta: ").append(sale.getSaleId()).append("\n");
                ticket.append("--------------------------------\n");

                // Encabezado de productos
                ticket.append("PRODUCTOS\n");
                ticket.append("--------------------------------\n");
                ticket.append(" \n");

                // --- Detalles ---
                for (SaleDetail detail : sale.getSaleDetails()) {

                        String productName = detail.getProductNameAtSale();
                        String quantity = detail.getProductQuantity().stripTrailingZeros().toPlainString();
                        String unit = detail.getUnitOfMeasureAtSale().getAbbreviation();

                        String unitPrice = "$" + detail.getSalePrice().setScale(2, RoundingMode.HALF_UP);
                        BigDecimal subtotal = detail.getSalePrice()
                                .multiply(detail.getProductQuantity())
                                .setScale(2, RoundingMode.HALF_UP);

                        String subtotalFormatted = "$" + subtotal;

                        // Ajustar nombre del producto si excede el ancho del comprobante
                        List<String> nameLines = wrapText(productName, 32);

                        for (String line : nameLines) {
                                ticket.append(line).append("\n");
                        }

                        // Construir línea principal del producto (cantidad, unidad, precio unitario, subtotal)
                        String itemLine = String.format(
                                "%s %s x %s = %s",
                                quantity,
                                unit,
                                unitPrice,
                                subtotalFormatted
                        );

                        // Ajustar línea completa para evitar desbordamiento en la impresora térmica
                        List<String> itemLines = wrapText(itemLine, 32);

                        for (String line : itemLines) {
                                ticket.append(line).append("\n");
                        }

                        // Espacio entre productos
                        ticket.append(" \n");

                }

                ticket.append("--------------------------------\n");

                // --- Total ---
                ticket.append(String.format(
                        "TOTAL: $%s\n",
                        sale.getTotalAmount().toPlainString()
                ));

                ticket.append(" \n");
                ticket.append(center("¡Gracias por su compra!")).append("\n");

                for (int i = 0; i < 4; i++) {
                        ticket.append(" \n");
                }

                ticket.append(" \n");

                return ticket.toString();
        }

        /**
         * Centra un texto para la impresión de comprobantes asumiendo un ancho fijo.
         *
         * @param text el texto a centrar
         * @return texto centrado rellenado con espacios
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
         * Divide un texto en múltiples líneas sin cortar palabras siempre que sea posible.
         *
         * <p>
         * Este método formatea el texto para que se ajuste a un diseño de ancho fijo
         * (como una impresora térmica), asegurando que las palabras se conserven y no
         * se dividan entre líneas, salvo que una palabra individual exceda el ancho
         * máximo permitido. En ese caso, la palabra se divide como mecanismo de respaldo.
         * </p>
         *
         * <p>
         * También normaliza los espacios en blanco, reemplazando múltiples espacios
         * consecutivos por un único espacio.
         * </p>
         *
         * @param text el texto que será dividido en líneas
         * @param width la cantidad máxima de caracteres por línea
         * @return una lista de líneas correctamente ajustadas al ancho especificado
         */
        private List<String> wrapText(String text, int width) {
                List<String> lines = new ArrayList<>();

                if (text == null || text.isBlank()) {
                        return lines;
                }

                String[] words = text.trim().split("\\s+");
                StringBuilder currentLine = new StringBuilder();

                for (String word : words) {

                        // Si la palabra por sí sola supera el ancho permitido, dividirla forzosamente
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

                        // Si la palabra cabe en la línea actual
                        if (currentLine.length() == 0) {
                                currentLine.append(word);
                        } else if (currentLine.length() + 1 + word.length() <= width) {
                                currentLine.append(" ").append(word);
                        } else {
                                // Pasar a la siguiente línea
                                lines.add(currentLine.toString());
                                currentLine.setLength(0);
                                currentLine.append(word);
                        }
                }

                // Agregar el contenido restante
                if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                }

                return lines;
        }

}
