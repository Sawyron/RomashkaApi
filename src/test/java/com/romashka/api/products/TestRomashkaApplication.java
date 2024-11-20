package com.romashka.api.products;

import com.romashka.api.RomashkaApiApplication;
import com.romashka.api.products.config.PostgresqlContainerTestConfiguration;
import org.springframework.boot.SpringApplication;

public class TestRomashkaApplication {
    public static void main(String[] args) {
        SpringApplication.from(RomashkaApiApplication::main)
                .with(PostgresqlContainerTestConfiguration.class)
                .run(args);
    }
}
