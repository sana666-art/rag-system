package com.rag_system.config;

import com.pgvector.PGvector;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class PgVectorConfig {

    private final DataSource dataSource;

    public PgVectorConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void registerVectorType() throws Exception {

        try (Connection connection = dataSource.getConnection()) {

            PGvector.addVectorType(connection);

            System.out.println("PGVector registered successfully.");
        }
    }
}