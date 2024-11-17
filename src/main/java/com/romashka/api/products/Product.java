package com.romashka.api.products;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "products")
public final class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(
            name = "description",
            nullable = false,
            length = 4096
    )
    private String description;

    @Column(
            name = "cost",
            nullable = false
    )
    private int cost;

    @Column(
            name = "is_available",
            nullable = false
    )
    private boolean isAvailable;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
