package com.example.kiki.repository;

import com.example.kiki.entity.CartActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartActivityRepository extends JpaRepository<CartActivity, Long> {

    @Query("SELECT ca.product.id AS productId, ca.product.name AS productName, " +
            "SUM(ca.quantity) AS totalAdds " +
            "FROM CartActivity ca " +
            "WHERE ca.organization.id = :organizationId " +
            "GROUP BY ca.product.id, ca.product.name " +
            "ORDER BY SUM(ca.quantity) DESC")
    List<ProductPopularityRow> findTopProductsForOrganization(@Param("organizationId") Long organizationId, Pageable pageable);

    interface ProductPopularityRow {
        Long getProductId();
        String getProductName();
        Long getTotalAdds();
    }
}