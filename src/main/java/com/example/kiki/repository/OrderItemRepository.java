package com.example.kiki.repository;

import com.example.kiki.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT COALESCE(SUM(oi.subTotal), 0) FROM OrderItem oi " +
            "WHERE oi.organization.id = :organizationId " +
            "AND oi.order.createdAt >= :from AND oi.order.createdAt < :to")
    BigDecimal sumRevenueForOrganizationBetween(@Param("organizationId") Long organizationId,
                                                @Param("from") LocalDateTime from,
                                                @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
            "WHERE oi.organization.id = :organizationId " +
            "AND oi.order.createdAt >= :from AND oi.order.createdAt < :to")
    Long sumUnitsForOrganizationBetween(@Param("organizationId") Long organizationId,
                                        @Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(DISTINCT oi.order.id) FROM OrderItem oi " +
            "WHERE oi.organization.id = :organizationId " +
            "AND oi.order.createdAt >= :from AND oi.order.createdAt < :to")
    Long countOrdersForOrganizationBetween(@Param("organizationId") Long organizationId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(DISTINCT oi.order.id) FROM OrderItem oi WHERE oi.organization.id = :organizationId")
    Long countAllOrdersForOrganization(@Param("organizationId") Long organizationId);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
            "WHERE oi.order.createdAt >= :from AND oi.order.createdAt < :to")
    Long sumUnitsBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @EntityGraph(attributePaths = {"order", "order.user", "product"})
    Page<OrderItem> findByOrganization_Id(Long organizationId, Pageable pageable);
}