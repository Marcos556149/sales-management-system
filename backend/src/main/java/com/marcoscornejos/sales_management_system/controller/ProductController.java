package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.StockLevelFilter;
import com.marcoscornejos.sales_management_system.service.IProductService;
import com.marcoscornejos.sales_management_system.model.ProductStatus;
import com.marcoscornejos.sales_management_system.model.SortOrder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * Controlador REST responsable de gestionar las consultas relacionadas
 * con productos.
 *
 * <p>
 * Proporciona endpoints para recuperar productos con capacidades opcionales
 * de filtrado, búsqueda y ordenamiento.
 * </p>
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService iProductService;

    /**
     * Recupera una lista paginada de productos con capacidades opcionales
     * de filtrado, búsqueda y ordenamiento.
     *
     * <p>
     * Soporta paginación del lado del servidor para gestionar eficientemente
     * grandes volúmenes de datos. Permite filtrar por estado del producto y
     * nivel de stock, buscar por código o nombre y ordenar por nombre del producto.
     * </p>
     *
     * @param searchCodeOrName código o nombre del producto (o parte de ellos)
     * @param statusFilter filtro por estado del producto (por defecto: ALL)
     * @param stockFilter filtro por nivel de stock (por defecto: ALL)
     * @param nameSort ordenamiento por nombre (por defecto: ASCENDING)
     * @param page número de página (por defecto: 0)
     * @param size cantidad de productos por página (por defecto: 50)
     * @return respuesta paginada que contiene los productos y los metadatos de paginación
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<PageResponseDTO<ProductListResponseDTO>> getProducts(
            @RequestParam(required = false) String searchCodeOrName,
            @RequestParam(defaultValue = "ALL") ProductStatus statusFilter,
            @RequestParam(defaultValue = "ALL") StockLevelFilter stockFilter,
            @RequestParam(defaultValue = "ASCENDING") SortOrder nameSort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {

        PageResponseDTO<ProductListResponseDTO> response = iProductService.getProducts(
                searchCodeOrName,
                statusFilter,
                stockFilter,
                nameSort,
                page,
                size
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Recupera una lista paginada de productos disponibles para la venta.
     *
     * <p>
     * Devuelve únicamente productos activos destinados a utilizarse en la
     * interfaz de registro de ventas. Permite buscar por código o nombre
     * del producto y ordenar por nombre.
     * </p>
     *
     * @param searchCodeOrName código o nombre del producto (o parte de ellos)
     * @param nameSort ordenamiento por nombre (por defecto: ASCENDING)
     * @param page número de página (por defecto: 0)
     * @param size cantidad de productos por página (por defecto: 10)
     * @return respuesta paginada que contiene los productos disponibles para la venta
     */
    @GetMapping("/sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<PageResponseDTO<ProductSaleListResponseDTO>> getProductsForSale(
            @RequestParam(required = false) String searchCodeOrName,
            @RequestParam(defaultValue = "ASCENDING") SortOrder nameSort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResponseDTO<ProductSaleListResponseDTO> response =
                iProductService.getProductsForSale(
                        searchCodeOrName,
                        nameSort,
                        page,
                        size
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Recupera las opciones disponibles de filtrado y ordenamiento para productos.
     *
     * <p>
     * Este endpoint proporciona datos de configuración dinámicos para el frontend,
     * incluyendo filtros por estado del producto y opciones de ordenamiento.
     * De esta forma se evita utilizar valores codificados de forma fija en la aplicación cliente.
     * </p>
     *
     * @return ProductFiltersResponseDTO que contiene las opciones de filtrado y ordenamiento
     */
    @GetMapping("/filters")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ProductFiltersResponseDTO> getFilters() {

        ProductFiltersResponseDTO response = iProductService.getFilters();

        return ResponseEntity.ok(response);
    }

    /**
     * Recupera la información detallada de un producto específico mediante su código.
     *
     * <p>
     * Este endpoint permite obtener los datos de un producto,
     * incluyendo su código, nombre, precio, unidad de medida, estado y stock.
     * </p>
     *
     * <p>
     * Posibles errores:
     * <ul>
     *   <li><b>PRODUCT_NOT_FOUND</b>: cuando el producto no existe</li>
     * </ul>
     * </p>
     *
     * @param productCode código único del producto
     * @return detalles del producto como DTO de respuesta
     */
    @GetMapping("/{productCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ProductDetailResponseDTO> getProductByCode(
            @PathVariable String productCode
    ) {

        ProductDetailResponseDTO product = iProductService.getProductByCode(productCode);

        return ResponseEntity.ok(product);
    }

    /**
     * Desactiva un producto (eliminación lógica).
     *
     * <p>
     * Esta operación actualiza el estado del producto a {@code INACTIVE} sin
     * eliminarlo físicamente de la base de datos. Tras la actualización, el método
     * devuelve la información actualizada del producto como un
     * {@link ProductDetailResponseDTO}.
     * </p>
     *
     * <p>
     * Esto evita una solicitud adicional del cliente para obtener el estado actualizado,
     * garantizando que la respuesta refleje el estado más reciente almacenado en la base de datos.
     * </p>
     *
     * @param productCode código único del producto
     * @return respuesta de éxito estandarizada que contiene los detalles actualizados del producto
     */
    @PatchMapping("/{productCode}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO<ProductDetailResponseDTO>> deactivateProduct(
            @PathVariable String productCode
    ) {

        ProductDetailResponseDTO product = iProductService.deactivateProduct(productCode);

        SuccessResponseDTO<ProductDetailResponseDTO> response = new SuccessResponseDTO<>(
                "PRODUCT_DEACTIVATED",
                "Producto desactivado correctamente",
                product
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Reactiva un producto.
     *
     * <p>
     * Esta operación actualiza el estado del producto a {@code ACTIVE},
     * dejándolo nuevamente disponible en el sistema. El método devuelve la
     * información actualizada del producto después del cambio de estado.
     * </p>
     *
     * @param productCode código único del producto
     * @return respuesta de éxito estandarizada que contiene los detalles actualizados del producto
     */
    @PatchMapping("/{productCode}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO<ProductDetailResponseDTO>> activateProduct(
            @PathVariable String productCode
    ) {

        ProductDetailResponseDTO product = iProductService.activateProduct(productCode);

        SuccessResponseDTO<ProductDetailResponseDTO> response = new SuccessResponseDTO<>(
                "PRODUCT_ACTIVATED",
                "Producto reactivado correctamente",
                product
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Registra un nuevo producto en el sistema.
     *
     * <p>
     * Este endpoint permite la creación de un nuevo producto proporcionando
     * la información requerida, como código, nombre, precio, unidad de medida
     * y stock. El estado del producto se establece automáticamente como ACTIVE.
     * </p>
     *
     * <p>
     * Si el código del producto ya existe, se devuelve un error.
     * </p>
     *
     * @param request datos del producto necesarios para crear un nuevo producto
     * @return respuesta de éxito estandarizada con el producto creado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO<ProductDetailResponseDTO>> registerProduct(
            @RequestBody @Valid ProductCreateRequestDTO request
    ) {

        ProductDetailResponseDTO createdProduct = iProductService.registerProduct(request);

        SuccessResponseDTO<ProductDetailResponseDTO> response = new SuccessResponseDTO<>(
                "PRODUCT_CREATED",
                "Producto registrado correctamente",
                createdProduct
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Recupera los metadatos necesarios para las operaciones relacionadas con productos.
     *
     * <p>
     * Incluye datos dinámicos, como las opciones de unidad de medida disponibles
     * utilizadas en los formularios de productos.
     * </p>
     *
     * @return metadatos de productos
     */
    @GetMapping("/metadata")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductMetadataResponseDTO> getProductMetadata() {

        ProductMetadataResponseDTO response=iProductService.getProductMetadata();

        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza un producto existente.
     *
     * <p>
     * Actualiza los datos del producto si este existe y la información
     * proporcionada es válida.
     * </p>
     *
     * @param productCode código único del producto a actualizar
     * @param request datos actualizados del producto
     * @return respuesta de éxito estandarizada que confirma la actualización
     */
    @PutMapping("/{productCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO<ProductDetailResponseDTO>> updateProduct(
            @PathVariable String productCode,
            @RequestBody @Valid ProductUpdateRequestDTO request
    ) {

        ProductDetailResponseDTO updatedProduct=iProductService.updateProduct(productCode, request);

        SuccessResponseDTO<ProductDetailResponseDTO> response = new SuccessResponseDTO<>(
                "PRODUCT_UPDATED",
                "Producto actualizado correctamente",
                updatedProduct
        );

        return ResponseEntity.ok(response);
    }
}
