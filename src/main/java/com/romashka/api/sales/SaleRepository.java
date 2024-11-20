package com.romashka.api.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
    @Query("select s from Sale s join fetch s.product where s.id = :id")
    Optional<Sale> findByIdWithProduct(UUID id);
}
