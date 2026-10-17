package com.example.bag.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:#{null}}")
    private String springDatasourceUrl;

    @Value("${spring.datasource.username:#{null}}")
    private String springDatasourceUsername;

    @Value("${spring.datasource.password:#{null}}")
    private String springDatasourcePassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        // 1. Check for Render / standard DATABASE_URL environment variable
        String envDbUrl = System.getenv("DATABASE_URL");
        if (envDbUrl == null || envDbUrl.trim().isEmpty()) {
            envDbUrl = System.getProperty("DATABASE_URL");
        }

        if (envDbUrl != null && !envDbUrl.trim().isEmpty()) {
            logger.info("Configuring PostgreSQL DataSource from DATABASE_URL environment variable...");
            return createDataSourceFromUrl(envDbUrl.trim());
        }

        // 2. Check if spring.datasource.url is explicitly provided
        if (springDatasourceUrl != null && !springDatasourceUrl.trim().isEmpty() && !springDatasourceUrl.startsWith("jdbc:h2:mem:")) {
            logger.info("Configuring DataSource from spring.datasource.url: {}", springDatasourceUrl);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(springDatasourceUrl);
            if (springDatasourceUsername != null) config.setUsername(springDatasourceUsername);
            if (springDatasourcePassword != null) config.setPassword(springDatasourcePassword);
            config.setDriverClassName("org.postgresql.Driver");
            applyConnectionPoolSettings(config);
            return new HikariDataSource(config);
        }

        // 3. Check individual parameters: DB_HOST, DB_PORT, DB_NAME, DB_USER/DB_USERNAME, DB_PASSWORD
        String dbHost = System.getenv("DB_HOST");
        if (dbHost != null && !dbHost.trim().isEmpty()) {
            String dbPort = System.getenv().getOrDefault("DB_PORT", "5432");
            String dbName = System.getenv().getOrDefault("DB_NAME", "bag");
            String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : System.getenv("DB_USERNAME");
            String dbPass = System.getenv("DB_PASSWORD");
            String jdbcUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

            logger.info("Configuring PostgreSQL DataSource from DB_HOST/DB_NAME: {}", jdbcUrl);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            if (dbUser != null) config.setUsername(dbUser);
            if (dbPass != null) config.setPassword(dbPass);
            config.setDriverClassName("org.postgresql.Driver");
            applyConnectionPoolSettings(config);
            return new HikariDataSource(config);
        }

        // 4. Fallback for local development or CI when no PostgreSQL instance is configured
        logger.warn("No PostgreSQL configuration detected. Falling back to local in-memory H2 database (PostgreSQL compatibility mode).");
        HikariConfig h2Config = new HikariConfig();
        h2Config.setJdbcUrl("jdbc:h2:mem:bagdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
        h2Config.setUsername("sa");
        h2Config.setPassword("");
        h2Config.setDriverClassName("org.h2.Driver");
        return new HikariDataSource(h2Config);
    }

    private DataSource createDataSourceFromUrl(String rawUrl) {
        HikariConfig config = new HikariConfig();

        if (rawUrl.startsWith("jdbc:")) {
            config.setJdbcUrl(rawUrl);
            if (springDatasourceUsername != null) config.setUsername(springDatasourceUsername);
            if (springDatasourcePassword != null) config.setPassword(springDatasourcePassword);
            config.setDriverClassName("org.postgresql.Driver");
            applyConnectionPoolSettings(config);
            return new HikariDataSource(config);
        }

        // Handle standard postgres:// or postgresql:// formats from Render
        String username = null;
        String password = null;
        String host = null;
        int port = 5432;
        String dbPath = null;
        String query = null;

        try {
            URI uri = new URI(rawUrl);
            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                String[] parts = userInfo.split(":", 2);
                username = parts[0];
                if (parts.length > 1) {
                    password = parts[1];
                }
            }
            host = uri.getHost();
            port = uri.getPort() != -1 ? uri.getPort() : 5432;
            dbPath = uri.getPath();
            query = uri.getQuery();
        } catch (Exception e) {
            logger.warn("URI parsing failed for DATABASE_URL (likely special characters in password). Falling back to regex parser.");
            // Regex fallback for postgres(ql)://user:password@host:port/database?query
            Pattern pattern = Pattern.compile("^postgres(?:ql)?://(?:([^:@]+)(?::([^@]+))?@)?([^:/]+)(?::(\\d+))?(/[^?]+)?(?:\\?(.*))?$");
            Matcher matcher = pattern.matcher(rawUrl);
            if (matcher.matches()) {
                username = matcher.group(1);
                password = matcher.group(2);
                host = matcher.group(3);
                String portStr = matcher.group(4);
                if (portStr != null) {
                    port = Integer.parseInt(portStr);
                }
                dbPath = matcher.group(5);
                query = matcher.group(6);
            }
        }

        if (host != null) {
            String path = (dbPath != null && !dbPath.isEmpty()) ? dbPath : "/bag";
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
            if (query != null && !query.trim().isEmpty()) {
                jdbcUrl += "?" + query;
            }

            logger.info("Configured PostgreSQL JDBC URL: jdbc:postgresql://{}:{}{}", host, port, path);
            config.setJdbcUrl(jdbcUrl);
            if (username != null) config.setUsername(username);
            if (password != null) config.setPassword(password);
            config.setDriverClassName("org.postgresql.Driver");
            applyConnectionPoolSettings(config);
            return new HikariDataSource(config);
        }

        // Direct fallback
        String cleanUrl = rawUrl.replaceFirst("^postgres://", "postgresql://");
        config.setJdbcUrl("jdbc:" + cleanUrl);
        config.setDriverClassName("org.postgresql.Driver");
        applyConnectionPoolSettings(config);
        return new HikariDataSource(config);
    }

    private void applyConnectionPoolSettings(HikariConfig config) {
        // Optimal settings for cloud environments like Render
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(20000);
        config.setMaxLifetime(1800000);
    }
}
