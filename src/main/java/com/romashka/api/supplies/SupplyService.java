package com.romashka.api.supplies;

import com.romashka.api.supplies.dtos.CreateSupplyRequest;
import com.romashka.api.supplies.dtos.SupplyResponse;
import com.romashka.api.supplies.dtos.UpdateSupplyRequest;

import java.util.List;
import java.util.UUID;

public interface SupplyService {
    UUID create(CreateSupplyRequest request);

    SupplyResponse findById(UUID id);

    List<SupplyResponse> findAll();

    SupplyResponse update(UUID id, UpdateSupplyRequest request);

    void deleteById(UUID id);
}
