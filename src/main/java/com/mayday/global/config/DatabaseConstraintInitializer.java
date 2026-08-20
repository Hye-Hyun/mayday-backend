package com.mayday.global.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseConstraintInitializer implements ApplicationRunner {

    private static final String POSTGRESQL = "PostgreSQL";

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseConstraintInitializer(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isPostgres()) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE expenses DROP CONSTRAINT IF EXISTS expenses_evidence_type_check");
        jdbcTemplate.execute("""
                ALTER TABLE expenses
                ADD CONSTRAINT expenses_evidence_type_check
                CHECK (evidence_type IN (
                    'CARD_RECEIPT',
                    'CASH_RECEIPT',
                    'TAX_INVOICE',
                    'INVOICE',
                    'NON_QUALIFIED',
                    'SIMPLE_RECEIPT',
                    'UNKNOWN'
                ))
                """);
    }

    private boolean isPostgres() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return POSTGRESQL.equalsIgnoreCase(connection.getMetaData().getDatabaseProductName());
        }
    }
}
