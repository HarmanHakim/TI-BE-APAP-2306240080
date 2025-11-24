package io.harman.flight_be.repository.loyalty;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.harman.flight_be.model.loyalty.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    List<Coupon> findAllByOrderByCreatedDateDesc();
}
