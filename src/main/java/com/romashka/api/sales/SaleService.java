package com.romashka.api.sales;

import com.romashka.api.sales.dtos.CreateSaleRequest;
import com.romashka.api.sales.dtos.SaleResponse;
import com.romashka.api.sales.dtos.UpdateSaleRequest;

import java.util.List;
import java.util.UUID;

public interface SaleService {
    UUID create(CreateSaleRequest request);

    SaleResponse findById(UUID id);

    List<SaleResponse> findAll();

    SaleResponse updateById(UUID id, UpdateSaleRequest request);

    void deleteById(UUID id);
}
