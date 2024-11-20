package com.romashka.api.supplies.impl;

import com.romashka.api.products.Product;
import com.romashka.api.products.ProductRepository;
import com.romashka.api.products.exceptions.ProductNotFoundException;
import com.romashka.api.supplies.Supply;
import com.romashka.api.supplies.SupplyRepository;
import com.romashka.api.supplies.SupplyService;
import com.romashka.api.supplies.dtos.CreateSupplyRequest;
import com.romashka.api.supplies.dtos.SupplyResponse;
import com.romashka.api.supplies.dtos.UpdateSupplyRequest;
import com.romashka.api.supplies.exceptions.SupplyNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
public class SupplyServiceImpl implements SupplyService {
    private final SupplyRepository supplyRepository;
    private final Function<Supply, SupplyResponse> responseMapper;
    private final ProductRepository productRepository;

    public SupplyServiceImpl(
            SupplyRepository supplyRepository,
            Function<Supply, SupplyResponse> responseMapper, ProductRepository productRepository
    ) {
        this.supplyRepository = supplyRepository;
        this.responseMapper = responseMapper;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public UUID create(CreateSupplyRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));
        var supply = new Supply();
        supply.setDocument(request.document());
        supply.setQuantity(request.quantity());
        supply.setProduct(product);
        product.setQuantity(product.getQuantity() + request.quantity());
        supplyRepository.save(supply);
        return supply.getId();
    }

    @Override
    public SupplyResponse findById(UUID id) {
        var supply = supplyRepository.findById(id)
                .orElseThrow(() -> new SupplyNotFoundException(id));
        return responseMapper.apply(supply);
    }

    @Override
    public List<SupplyResponse> findAll() {
        return supplyRepository.findAll()
                .stream()
                .map(responseMapper)
                .toList();
    }

    @Override
    @Transactional
    public SupplyResponse update(UUID id, UpdateSupplyRequest request) {
        Supply supply = supplyRepository.findByIdWithProduct(id)
                .orElseThrow(() -> new SupplyNotFoundException(id));
        Product product = supply.getProduct().getId().equals(request.productId())
                ? supply.getProduct()
                : productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(id));
        int productCountDiff = supply.getProduct().getId().equals(request.productId())
                ? supply.getQuantity() - request.quantity()
                : -request.quantity();
        if (!supply.getProduct().getId().equals(request.productId())) {
            Product oldProduct = supply.getProduct();
            oldProduct.setQuantity(oldProduct.getQuantity() - supply.getQuantity());
        }
        supply.setDocument(request.document());
        supply.setQuantity(request.quantity());
        supply.setProduct(product);
        product.setQuantity(product.getQuantity() - productCountDiff);
        return responseMapper.apply(supply);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Supply supply = supplyRepository.findByIdWithProduct(id)
                .orElseThrow(() -> new SupplyNotFoundException(id));
        Product product = supply.getProduct();
        product.setQuantity(product.getQuantity() - supply.getQuantity());
        supplyRepository.delete(supply);
    }
}
