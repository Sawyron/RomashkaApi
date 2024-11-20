package com.romashka.api.supplies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SupplyRepository extends JpaRepository<Supply, UUID> {
    @Query("select SUM(s.quantity) from Supply s where s.product.id = :productId")
    long countProductSupplies(UUID productId);

    @Query("select s from Supply s join fetch s.product where s.id = :id")
    Optional<Supply> findByIdWithProduct(UUID id);
}
