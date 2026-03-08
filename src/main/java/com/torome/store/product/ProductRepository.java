package com.torome.store.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query(value = """
        SELECT * FROM products
        WHERE LOWER(name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(description) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(CAST(metadata AS VARCHAR)) LIKE LOWER(CONCAT('%', :query, '%'))
        LIMIT :topK
        """, nativeQuery = true)
    List<ProductEntity> searchByKeyword(@Param("query") String query, @Param("topK") int topK);
}
