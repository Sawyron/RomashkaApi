package com.romashka.api.sales;

import com.romashka.api.sales.dtos.CreateSaleRequest;
import com.romashka.api.sales.dtos.SaleResponse;
import com.romashka.api.sales.dtos.UpdateSaleRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid CreateSaleRequest request) {
        UUID id = saleService.create(request);
        URI url = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(url).body(id);
    }

    @GetMapping("{id}")
    public ResponseEntity<SaleResponse> findById(@PathVariable UUID id) {
        SaleResponse sale = saleService.findById(id);
        return ResponseEntity.ok(sale);
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> findAll() {
        List<SaleResponse> sales = saleService.findAll();
        return ResponseEntity.ok(sales);
    }

    @PutMapping("{id}")
    public ResponseEntity<SaleResponse> update(@PathVariable UUID id, @RequestBody @Valid UpdateSaleRequest request) {
        SaleResponse sale = saleService.updateById(id, request);
        return ResponseEntity.ok(sale);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        saleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
