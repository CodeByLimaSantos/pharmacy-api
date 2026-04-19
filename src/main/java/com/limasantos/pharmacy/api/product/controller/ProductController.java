package com.limasantos.pharmacy.api.product.controller;

import com.limasantos.pharmacy.api.dto.response.base.ApiResponse;
import com.limasantos.pharmacy.api.dto.response.base.DeleteResponse;
import com.limasantos.pharmacy.api.dto.response.base.PaginatedResponse;
import com.limasantos.pharmacy.api.dto.response.domain.product.ProductListResponse;
import com.limasantos.pharmacy.api.dto.response.domain.product.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.limasantos.pharmacy.api.product.dto.CreateProductDTO;
import com.limasantos.pharmacy.api.product.dto.ProductDTO;
import com.limasantos.pharmacy.api.product.dto.ProductDetailDTO;
import com.limasantos.pharmacy.api.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Produtos", description = "Cadastro de produtos, filtros por fornecedor e controle de medicamentos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Criar produto", description = "Cadastra um novo produto com dados comerciais e categoria.")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody CreateProductDTO dto,
                                                               HttpServletRequest request) {
        ProductDTO created = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProductResponse>builder()
                        .success(true)
                        .message("Produto criado com sucesso")
                        .code("PRODUCT_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(ProductResponse.fromDto(created))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os dados resumidos de um produto específico.")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id,
                                                                 HttpServletRequest request) {
        ProductDTO product = productService.findById(id);
        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .success(true)
                        .message("Produto encontrado")
                        .code("PRODUCT_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(ProductResponse.fromDto(product))
                        .build()
        );
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "Buscar detalhes do produto", description = "Retorna dados detalhados incluindo margem e movimentos de estoque.")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> findDetailById(@PathVariable Long id,
                                                                         HttpServletRequest request) {
        ProductDetailDTO detail = productService.findDetailById(id);
        return ResponseEntity.ok(
                ApiResponse.<ProductDetailDTO>builder()
                        .success(true)
                        .message("Detalhes do produto encontrados")
                        .code("PRODUCT_DETAIL_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(detail)
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos em envelope paginado padrão.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de produtos", content = @Content(schema = @Schema(implementation = PaginatedResponse.class)))
    })
    public ResponseEntity<PaginatedResponse<ProductListResponse>> findAll(HttpServletRequest request) {
        List<ProductListResponse> products = productService.findAll().stream()
                .map(ProductListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<ProductListResponse>paginatedBuilder()
                        .success(true)
                        .message("Produtos recuperados com sucesso")
                        .code("PRODUCTS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(products)
                        .page(0)
                        .pageSize(products.size())
                        .totalElements(products.size())
                        .totalPages(products.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Listar produtos por fornecedor", description = "Filtra produtos vinculados a um fornecedor específico.")
    public ResponseEntity<PaginatedResponse<ProductListResponse>> findBySupplier(@PathVariable Long supplierId,
                                                                                  HttpServletRequest request) {
        List<ProductListResponse> products = productService.findBySupplier(supplierId).stream()
                .map(ProductListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<ProductListResponse>paginatedBuilder()
                        .success(true)
                        .message("Produtos do fornecedor recuperados")
                        .code("SUPPLIER_PRODUCTS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(products)
                        .page(0)
                        .pageSize(products.size())
                        .totalElements(products.size())
                        .totalPages(products.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/controlled")
    @Operation(summary = "Listar produtos controlados", description = "Retorna apenas produtos com controle especial.")
    public ResponseEntity<PaginatedResponse<ProductListResponse>> findControlledProducts(HttpServletRequest request) {
        List<ProductListResponse> products = productService.findControlledProducts().stream()
                .map(ProductListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<ProductListResponse>paginatedBuilder()
                        .success(true)
                        .message("Produtos controlados recuperados")
                        .code("CONTROLLED_PRODUCTS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(products)
                        .page(0)
                        .pageSize(products.size())
                        .totalElements(products.size())
                        .totalPages(products.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente.")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody CreateProductDTO dto,
                                                               HttpServletRequest request) {
        ProductDTO updated = productService.update(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .success(true)
                        .message("Produto atualizado com sucesso")
                        .code("PRODUCT_UPDATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(ProductResponse.fromDto(updated))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto", description = "Exclui um produto e retorna confirmação da operação.")
    public ResponseEntity<ApiResponse<DeleteResponse>> delete(@PathVariable Long id,
                                                              HttpServletRequest request) {
        productService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<DeleteResponse>builder()
                        .success(true)
                        .message("Produto deletado com sucesso")
                        .code("PRODUCT_DELETED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(new DeleteResponse(id, true))
                        .build()
        );
    }

    private String resolvePath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;
    }
}
