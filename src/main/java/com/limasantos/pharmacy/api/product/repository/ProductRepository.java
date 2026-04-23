package com.limasantos.pharmacy.api.product.repository;

import com.limasantos.pharmacy.api.category.entity.ProductCategoryType;
import com.limasantos.pharmacy.api.product.entity.Product;
import com.limasantos.pharmacy.api.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca produtos por fornecedor
     */
    List<Product> findBySupplier(Supplier supplier);

    /**
     * Busca produtos controlados
     */
    List<Product> findByControlledTrue();

    /**
     * Busca produtos por categoria
     */
    List<Product> findByProductCategoryType(ProductCategoryType category);

    /**
     * Busca produtos por nome (case-insensitive)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
}

