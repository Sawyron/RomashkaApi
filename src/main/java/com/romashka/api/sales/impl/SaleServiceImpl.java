package com.romashka.api.sales.impl;

import com.romashka.api.products.Product;
import com.romashka.api.products.ProductRepository;
import com.romashka.api.products.exceptions.ProductNotFoundException;
import com.romashka.api.sales.Sale;
import com.romashka.api.sales.SaleRepository;
import com.romashka.api.sales.SaleService;
import com.romashka.api.sales.dtos.CreateSaleRequest;
import com.romashka.api.sales.dtos.SaleResponse;
import com.romashka.api.sales.dtos.UpdateSaleRequest;
import com.romashka.api.sales.exceptions.SaleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
public class SaleServiceImpl implements SaleService {
    private final SaleRepository saleRepository;
    private final Function<Sale, SaleResponse> responseMapper;
    private final ProductRepository productRepository;

    public SaleServiceImpl(
            SaleRepository saleRepository,
            Function<Sale, SaleResponse> responseMapper,
            ProductRepository productRepository
    ) {
        this.saleRepository = saleRepository;
        this.responseMapper = responseMapper;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public UUID create(CreateSaleRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));
        if (product.getQuantity() - request.quantity() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to create sale. No enough products"
            );
        }
        var sale = new Sale();
        sale.setDocument(request.document());
        sale.setQuantity(request.quantity());
        sale.setProduct(product);
        sale.setTotalPrice(product.getPrice() * request.quantity());
        product.setQuantity(product.getQuantity() - request.quantity());
        saleRepository.save(sale);
        return sale.getId();
    }

    @Override
    public SaleResponse findById(UUID id) {
        Sale sale = saleRepository.findByIdWithProduct(id)
                .orElseThrow(() -> new SaleNotFoundException(id));
        return responseMapper.apply(sale);
    }

    @Override
    public List<SaleResponse> findAll() {
        return saleRepository.findAll()
                .stream()
                .map(responseMapper)
                .toList();
    }

    @Override
    @Transactional
    public SaleResponse updateById(UUID id, UpdateSaleRequest request) {
        Sale sale = saleRepository.findByIdWithProduct(id)
                .orElseThrow(() -> new SaleNotFoundException(id));
        Product product = sale.getProduct().getId().equals(request.productId())
                ? sale.getProduct()
                : productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));
        int productQuantityDiff = sale.getProduct().getId().equals(request.productId())
                ? sale.getQuantity() - request.quantity()
                : -request.quantity();
        if (product.getQuantity() + productQuantityDiff < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to delete sale. This lead to negative product count"
            );
        }
        if (!sale.getProduct().getId().equals(product.getId())) {
            Product oldProduct = sale.getProduct();
            oldProduct.setQuantity(oldProduct.getQuantity() + sale.getQuantity());
        }
        sale.setProduct(product);
        sale.setDocument(request.document());
        sale.setQuantity(request.quantity());
        sale.setTotalPrice(product.getPrice() * request.quantity());
        product.setQuantity(product.getQuantity() + productQuantityDiff);
        saleRepository.save(sale);
        productRepository.save(product);
        return responseMapper.apply(sale);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Sale sale = saleRepository.findByIdWithProduct(id)
                .orElseThrow(() -> new SaleNotFoundException(id));
        Product product = sale.getProduct();
        product.setQuantity(product.getQuantity() + sale.getQuantity());
        saleRepository.delete(sale);
    }
}
