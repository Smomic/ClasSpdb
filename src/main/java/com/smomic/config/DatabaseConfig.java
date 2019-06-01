package com.smomic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@ComponentScan(basePackages = "com.smomic")
@PropertySource(value = { "classpath:application.properties" })
public class DatabaseConfig {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource managerDataSource = new DriverManagerDataSource();
        managerDataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driver"));
        managerDataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        managerDataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        managerDataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
        return managerDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) throws SQLException {
        checkConnection();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }

    private void checkConnection() throws SQLException {
        dataSource().getConnection();
    }
}
