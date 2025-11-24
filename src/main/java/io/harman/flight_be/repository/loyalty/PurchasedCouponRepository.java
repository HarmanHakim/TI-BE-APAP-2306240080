package io.harman.flight_be.repository.loyalty;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.harman.flight_be.model.loyalty.PurchasedCoupon;

@Repository
public interface PurchasedCouponRepository extends JpaRepository<PurchasedCoupon, UUID> {
    List<PurchasedCoupon> findByCustomerIdOrderByPurchasedDateDesc(UUID customerId);
    Optional<PurchasedCoupon> findByCode(String code);
    long countByCustomerIdAndCouponId(UUID customerId, UUID couponId);
    long countByCustomerId(UUID customerId);
    long countByCustomerIdAndUsedDateIsNull(UUID customerId);
    long countByCustomerIdAndUsedDateIsNotNull(UUID customerId);
    boolean existsByCode(String code);
}
