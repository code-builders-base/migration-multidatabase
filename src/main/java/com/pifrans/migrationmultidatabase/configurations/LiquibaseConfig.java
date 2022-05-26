package com.pifrans.migrationmultidatabase.configurations;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class LiquibaseConfig {
    private static final Logger LOG = LoggerFactory.getLogger(LiquibaseConfig.class);


    @PostConstruct
    public void init() {
        var databases = getDatabases();

        for (String url : databases) {
            try (var connection = getConnection(url, "root", "root")) {
                LOG.info("Iniciando migration no banco {}", url);
                createSchema(connection);
                var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                database.setDefaultSchemaName("liquibase");
                database.setLiquibaseSchemaName("liquibase");

                try (var liquibase = new liquibase.Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database)) {
                    liquibase.update(new Contexts(), new LabelExpression());
                    LOG.info("Migration finalizada no banco {}", url);
                }
            } catch (Exception e) {
                LOG.error("Erro ao fazer migration no banco {}", url);
                e.printStackTrace();
            }
        }
    }

    private void createSchema(Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS liquibase");
        }
    }

    private Connection getConnection(String url, String user, String pass) throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    private List<String> getDatabases() {
        try (Connection connection = getConnection("jdbc:postgresql://localhost:5432/main", "root", "root")) {
            try (var statement = connection.createStatement()) {
                statement.execute("SELECT url FROM databases");
                var resultSet = statement.getResultSet();
                var urls = new ArrayList<String>();

                while (resultSet.next()) {
                    urls.add(resultSet.getString("url"));
                }
                return urls;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
