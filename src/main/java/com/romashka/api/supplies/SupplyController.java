package com.romashka.api.supplies;

import com.romashka.api.supplies.dtos.CreateSupplyRequest;
import com.romashka.api.supplies.dtos.SupplyResponse;
import com.romashka.api.supplies.dtos.UpdateSupplyRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/supplies")
public class SupplyController {
    private final SupplyService supplyService;

    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid CreateSupplyRequest request) {
        UUID id = supplyService.create(request);
        URI url = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(url).body(id);
    }

    @GetMapping("{id}")
    public ResponseEntity<SupplyResponse> getById(@PathVariable UUID id) {
        SupplyResponse supply = supplyService.findById(id);
        return ResponseEntity.ok(supply);
    }

    @GetMapping
    public ResponseEntity<List<SupplyResponse>> getAll() {
        List<SupplyResponse> supplies = supplyService.findAll();
        return ResponseEntity.ok(supplies);
    }

    @PutMapping("{id}")
    public ResponseEntity<SupplyResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateSupplyRequest request
    ) {
        SupplyResponse supply = supplyService.update(id, request);
        return ResponseEntity.ok(supply);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        supplyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
