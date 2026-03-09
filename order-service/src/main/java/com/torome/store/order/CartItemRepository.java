package com.torome.store.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByUserIdOrderByCreatedAtAsc(Long userId);

    @Modifying
    @Query("DELETE FROM CartItemEntity c WHERE c.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
