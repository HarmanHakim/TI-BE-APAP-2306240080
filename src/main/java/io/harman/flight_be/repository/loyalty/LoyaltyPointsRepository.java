package io.harman.flight_be.repository.loyalty;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.harman.flight_be.model.loyalty.LoyaltyPoints;

@Repository
public interface LoyaltyPointsRepository extends JpaRepository<LoyaltyPoints, UUID> {
    Optional<LoyaltyPoints> findByCustomerId(UUID customerId);
}
