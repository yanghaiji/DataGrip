package com.javayh.data.grip.jdbc.cdc.configuration.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author haiji
 */
@Configuration
public class DataSourceConfiguration {


    @Primary
    @Bean(name = "sourceDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.source")
    public HikariDataSource queryDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "targetDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.target")
    public HikariDataSource storyDataSource() {
        return new HikariDataSource();
    }


}