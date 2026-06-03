package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.exception.InvalidProductDataException;
import com.marcoscornejos.sales_management_system.exception.ProductAlreadyExistsException;
import com.marcoscornejos.sales_management_system.exception.ProductNotFoundException;
import com.marcoscornejos.sales_management_system.mapper.*;
import com.marcoscornejos.sales_management_system.model.*;
import com.marcoscornejos.sales_management_system.repository.IProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


/**
 * Servicio responsable de recuperar productos aplicando búsqueda,
 * filtrado y ordenamiento a nivel de base de datos.
 */
@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final IProductRepository iProductRepository;
    private final IProductListResponseMapper iProductListResponseMapper;
    private final IPageResponseMapper iPageResponseMapper;
    private final IProductDetailResponseMapper iProductDetailResponseMapper;
    private final IProductCreateRequestMapper iProductCreateRequestMapper;
    private final IProductUpdateRequestMapper iProductUpdateRequestMapper;
    private final IProductSaleListResponseMapper iProductSaleListResponseMapper;

    /**
     * Recupera una lista paginada de productos aplicando:
     * <ul>
     * <li>Búsqueda por nombre o código</li>
     * <li>Filtrado por estado (ALL = sin filtro)</li>
     * <li>Filtrado por nivel de stock (ALL = sin filtro)</li>
     * <li>Ordenamiento por nombre</li>
     * <li>Paginación (número de página y tamaño de página)</li>
     * </ul>
     *
     * <p>
     * La búsqueda es opcional y se ignora si es null o está vacía.
     * La paginación y el ordenamiento se ejecutan a nivel de base de datos
     * (paginación del lado del servidor).
     * </p>
     *
     * @param searchCodeOrName término de búsqueda opcional
     * @param statusFilter filtro por estado del producto
     * @param stockFilter filtro por nivel de stock del producto
     * @param nameSort orden de clasificación (ASCENDING / DESCENDING)
     * @param page número de página (base 0)
     * @param size cantidad de elementos por página
     * @return lista paginada de productos mapeada a DTO
     */
    @Override
    public PageResponseDTO<ProductListResponseDTO> getProducts(String searchCodeOrName,
                                                               ProductStatus statusFilter,
                                                               StockLevelFilter stockFilter,
                                                               SortOrder nameSort,
                                                               int page,
                                                               int size) {

        // Validar parámetros de paginación
        if (page < 0) {
            throw new InvalidProductDataException(
                    "La página no puede ser negativa",
                    "page"
            );
        }

        if (size <= 0) {
            throw new InvalidProductDataException(
                    "El tamaño de página debe ser mayor que cero",
                    "size"
            );
        }

        if (size > 50) {
            throw new InvalidProductDataException(
                    "El tamaño de página no puede ser mayor que 50",
                    "size"
            );
        }

        // Construir la configuración de ordenamiento (aplicada a nivel de base de datos)
        Sort sort = Sort.by("productName");

        if (nameSort == SortOrder.DESCENDING) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        // Normalizar la entrada de búsqueda:
        // Convertir cadenas vacías o compuestas únicamente por espacios en null
        // para que la consulta ignore el filtro de búsqueda
        if (searchCodeOrName != null && searchCodeOrName.trim().isEmpty()) {
            searchCodeOrName = null;
        }


        // Crear la configuración de paginación incluyendo el número de página,
        // el tamaño de página y los criterios de ordenamiento que se aplicarán
        // a nivel de base de datos
        Pageable pageable = PageRequest.of(page, size, sort);

        // Ejecutar la consulta aplicando búsqueda, filtro por estado,
        // filtro por nivel de stock y configuración de paginación
        Page<Product> productPage = iProductRepository.findProducts(
                searchCodeOrName,
                statusFilter,
                stockFilter.name(),
                pageable
        );

        // Cantidad total de productos en la base de datos sin aplicar filtros.
        // Este valor solo se calcula cuando la consulta filtrada no devuelve resultados,
        // permitiendo al frontend distinguir entre:
        //
        // 1) No existen productos en la base de datos
        // 2) Existen productos, pero ninguno coincide con los criterios actuales
        //    de búsqueda o filtrado
        //
        // Cuando la consulta filtrada devuelve resultados, este valor permanece en null
        // para evitar una consulta COUNT(*) adicional innecesaria y mejorar el rendimiento.
        Long totalGlobalElements = null;

        if (productPage.getTotalElements() == 0) {
            totalGlobalElements = iProductRepository.count();
        }

        // Mapear entidades a DTOs
        return iPageResponseMapper.toPageResponseDTO(
                productPage.getContent()
                        .stream()
                        .map(iProductListResponseMapper::toDto)
                        .toList(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalPages(),
                productPage.getTotalElements(),
                totalGlobalElements
        );
    }

    /**
     * Recupera una lista paginada de productos disponibles para la venta aplicando:
     * <ul>
     * <li>Búsqueda por nombre o código del producto</li>
     * <li>Solo se incluyen productos con estado ACTIVE</li>
     * <li>Solo se incluyen productos con stock mayor que cero</li>
     * <li>Ordenamiento por nombre del producto</li>
     * <li>Paginación (número de página y tamaño de página)</li>
     * </ul>
     *
     * <p>
     * La búsqueda es opcional y se ignora si es null o está vacía.
     * La paginación y el ordenamiento se ejecutan a nivel de base de datos
     * (paginación del lado del servidor).
     * </p>
     *
     * @param searchCodeOrName término de búsqueda opcional
     * @param nameSort orden de clasificación (ASCENDING / DESCENDING)
     * @param page número de página (base 0)
     * @param size cantidad de elementos por página
     * @return lista paginada de productos disponibles para la venta mapeada a DTO
     */
    @Override
    public PageResponseDTO<ProductSaleListResponseDTO> getProductsForSale(
            String searchCodeOrName,
            SortOrder nameSort,
            int page,
            int size
    ) {

        // Validar parámetros de paginación
        if (page < 0) {
            throw new InvalidProductDataException(
                    "Page index must not be negative",
                    "page"
            );
        }

        if (size <= 0) {
            throw new InvalidProductDataException(
                    "Page size must be greater than zero",
                    "size"
            );
        }

        if (size > 50) {
            throw new InvalidProductDataException(
                    "Page size must not exceed 50",
                    "size"
            );
        }

        // Construir la configuración de ordenamiento (aplicada a nivel de base de datos)
        Sort sort = Sort.by("productName");

        if (nameSort == SortOrder.DESCENDING) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        // Normalizar la entrada de búsqueda:
        // Convertir cadenas vacías o compuestas únicamente por espacios en null
        // para que la consulta ignore el filtro de búsqueda
        if (searchCodeOrName != null && searchCodeOrName.trim().isEmpty()) {
            searchCodeOrName = null;
        }

        // Crear la configuración de paginación incluyendo el número de página,
        // el tamaño de página y los criterios de ordenamiento
        Pageable pageable = PageRequest.of(page, size, sort);

        // Ejecutar la consulta:
        // solo productos con estado ACTIVE y stock mayor que cero
        Page<Product> productPage = iProductRepository.findProductsForSale(
                searchCodeOrName,
                pageable
        );

        // Cantidad total de productos disponibles para la venta.
        // Solo se calcula cuando la consulta filtrada no devuelve resultados.
        Long totalGlobalElements = null;

        if (productPage.getTotalElements() == 0) {
            totalGlobalElements =
                    iProductRepository.countByProductStatusAndProductStockGreaterThan(
                            ProductStatus.ACTIVE,
                            BigDecimal.ZERO
                    );
        }

        // Mapear entidades a DTOs
        return iPageResponseMapper.toPageResponseDTO(
                productPage.getContent()
                        .stream()
                        .map(iProductSaleListResponseMapper::toDto)
                        .toList(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalPages(),
                productPage.getTotalElements(),
                totalGlobalElements
        );
    }


    /**
     * Construye y devuelve las opciones de filtrado y ordenamiento disponibles para productos.
     *
     * <p>
     * Este método obtiene los valores de las enumeraciones {@link ProductStatus},
     * {@link SortOrder} y {@link StockLevelFilter}, y los convierte al formato
     * {@link EnumDTO}, adecuado para su consumo por el frontend.
     * </p>
     *
     * <p>
     * Garantiza que el frontend reciba siempre las opciones más actualizadas
     * sin requerir cambios en el código del cliente, actuando como una única
     * fuente de verdad para las capacidades de filtrado de productos.
     * </p>
     *
     * @return ProductFiltersResponseDTO que contiene las opciones de estado,
     *         ordenamiento y filtrado por stock
     */
    @Override
    public ProductFiltersResponseDTO getFilters() {

        List<EnumDTO> statusOptions = Arrays.stream(ProductStatus.values())
                .map(status -> new EnumDTO(
                        status.name(),
                        status.getDisplayName()
                ))
                .toList();

        List<EnumDTO> nameSortOptions = Arrays.stream(SortOrder.values())
                .map(sort -> new EnumDTO(
                        sort.name(),
                        sort.getDisplayName()
                ))
                .toList();

        List<EnumDTO> stockLevelOptions = Arrays.stream(StockLevelFilter.values())
                .map(stock -> new EnumDTO(
                        stock.name(),
                        stock.getDisplayName()
                ))
                .toList();

        return new ProductFiltersResponseDTO(
                statusOptions,
                nameSortOptions,
                stockLevelOptions
        );
    }

    /**
     * Recupera un producto mediante su código único.
     *
     * <p>
     * Busca un producto en la base de datos utilizando el código proporcionado.
     * Lanza una excepción si el producto no existe.
     * </p>
     *
     * @param productCode identificador único del producto
     * @return los datos del producto como DTO
     * @throws ProductNotFoundException si el producto no es encontrado
     */
    @Override
    public ProductDetailResponseDTO getProductByCode(String productCode) {

        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("No se encontró el producto con código '%s'", productCode)
                ));

        return iProductDetailResponseMapper.toDto(product);
    }

    /**
     * Desactiva un producto estableciendo su estado en INACTIVE.
     *
     * <p>
     * Esta operación realiza una eliminación lógica. Si el producto no existe
     * o ya se encuentra inactivo, se lanza una excepción de negocio.
     * </p>
     *
     * <p>
     * La operación se ejecuta dentro de un contexto transaccional para garantizar
     * la atomicidad de la actualización del estado.
     * </p>
     *
     * @param productCode identificador único del producto
     * @throws ProductNotFoundException si el producto no existe
     * @throws InvalidProductDataException si el producto ya se encuentra inactivo
     */
    @Override
    @Transactional
    public ProductDetailResponseDTO deactivateProduct(String productCode) {

        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("No se encontró el producto con código '%s'", productCode)
                ));

        if (product.getProductStatus() == ProductStatus.INACTIVE) {
            throw new InvalidProductDataException(
                    String.format("El producto con código '%s' ya se encuentra inactivo", productCode)
            );
        }

        product.setProductStatus(ProductStatus.INACTIVE);

        // Persistir cambios
        iProductRepository.save(product);

        // Mapear directamente desde la entidad gestionada (no es necesario volver a consultarla)
        return iProductDetailResponseMapper.toDto(product);
    }

    /**
     * Reactiva un producto estableciendo su estado en ACTIVE.
     *
     * <p>
     * Esta operación restaura un producto previamente desactivado. Si el producto no existe
     * o ya se encuentra activo, se lanza una excepción de negocio.
     * </p>
     *
     * <p>
     * La operación se ejecuta dentro de un contexto transaccional para garantizar
     * la atomicidad de la actualización del estado.
     * </p>
     *
     * @param productCode identificador único del producto
     * @throws ProductNotFoundException si el producto no existe
     * @throws InvalidProductDataException si el producto ya se encuentra activo
     */
    @Override
    @Transactional
    public ProductDetailResponseDTO activateProduct(String productCode) {

        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("No se encontró el producto con código '%s'", productCode)
                ));

        if (product.getProductStatus() == ProductStatus.ACTIVE) {
            throw new InvalidProductDataException(
                    String.format("El producto con código '%s' ya se encuentra activo", productCode)
            );
        }

        product.setProductStatus(ProductStatus.ACTIVE);

        // Persistir cambios
        iProductRepository.save(product);

        // Mapear directamente desde la entidad gestionada (no es necesario volver a consultarla)
        return iProductDetailResponseMapper.toDto(product);
    }

    /**
     * Registra un nuevo producto en el sistema.
     *
     * <p>
     * Esta operación valida que el producto no exista previamente
     * y lo almacena en la base de datos. El estado del producto se establece
     * automáticamente en ACTIVE.
     * </p>
     *
     * <p>
     * Si ya existe un producto con el mismo código o se incumplen las reglas
     * de validación de negocio, se lanza una excepción de negocio.
     * </p>
     *
     * <p>
     * La operación se ejecuta dentro de un contexto transaccional para garantizar
     * la atomicidad de la creación del producto.
     * </p>
     *
     * @param request solicitud de creación del producto que contiene sus datos
     * @throws ProductAlreadyExistsException si ya existe un producto con el mismo código
     * @throws InvalidProductDataException si se incumplen las reglas de validación de negocio
     */
    @Override
    @Transactional
    public ProductDetailResponseDTO registerProduct(ProductCreateRequestDTO request) {

        // Verificar si el producto ya existe
        if (iProductRepository.existsById(request.getProductCode())) {
            throw new ProductAlreadyExistsException(
                    String.format("Ya existe un producto con el código '%s'", request.getProductCode())
            );
        }

        // Validar el formato de stock y stock mínimo cuando la unidad de medida es UNITS
        if (request.getUnitOfMeasure() == UnitOfMeasure.UNITS) {
            if (request.getProductStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "El stock debe ser un valor entero cuando la unidad de medida es 'Unidades'",
                        "productStock"
                );
            }

            if (request.getMinimumStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "El stock mínimo debe ser un valor entero cuando la unidad de medida es 'Unidades'",
                        "minimumStock"
                );
            }
        }

        // Mapear DTO a entidad
        Product product = iProductCreateRequestMapper.toProduct(request);

        // Persistir producto
        iProductRepository.save(product);

        // Mapear directamente desde la entidad gestionada (no es necesario volver a consultarla)
        return iProductDetailResponseMapper.toDto(product);
    }

    /**
     * Recupera los metadatos necesarios para las operaciones relacionadas con productos.
     *
     * <p>
     * Incluye las opciones de unidad de medida disponibles para la creación de productos.
     * </p>
     *
     * @return metadatos de productos
     */
    @Override
    public ProductMetadataResponseDTO getProductMetadata() {

        List<EnumDTO> unitOfMeasureOptions = Arrays.stream(UnitOfMeasure.values())
                .map(unit -> new EnumDTO(
                        unit.name(),
                        unit.getDisplayName()
                ))
                .toList();

        return new ProductMetadataResponseDTO(unitOfMeasureOptions);
    }

    /**
     * Actualiza un producto existente en el sistema.
     *
     * <p>
     * Esta operación valida que el producto exista y aplica las reglas de negocio
     * antes de persistir los datos actualizados.
     * El producto se actualiza de forma atómica dentro de un contexto transaccional.
     * </p>
     *
     * @param productCode código del producto a actualizar
     * @param request datos actualizados del producto
     * @return detalles del producto actualizado
     * @throws ProductNotFoundException si el producto no existe
     * @throws InvalidProductDataException si se incumplen las reglas de negocio
     */
    @Override
    @Transactional
    public ProductDetailResponseDTO updateProduct(String productCode, ProductUpdateRequestDTO request) {

        // 1. Verificar que el producto exista
        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("No se encontró el producto con código '%s'", productCode)
                ));

        // 2. Validar las reglas de stock y stock mínimo cuando la unidad de medida es UNITS
        if (request.getUnitOfMeasure() == UnitOfMeasure.UNITS) {

            if (request.getProductStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "El stock debe ser un valor entero cuando la unidad de medida es 'Unidades'",
                        "productStock"
                );
            }

            if (request.getMinimumStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "El stock mínimo debe ser un valor entero cuando la unidad de medida es 'Unidades'",
                        "minimumStock"
                );
            }
        }

        // 3. Aplicar las modificaciones
        iProductUpdateRequestMapper.updateProductFromDto(request, product);

        // 4. Persistir cambios
        iProductRepository.save(product);

        // 5. Mapear directamente desde la entidad gestionada (no es necesario volver a consultarla)
        return iProductDetailResponseMapper.toDto(product);
    }
}