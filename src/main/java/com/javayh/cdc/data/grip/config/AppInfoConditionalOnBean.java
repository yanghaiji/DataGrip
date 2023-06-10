package com.javayh.cdc.data.grip.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-05-30
 */
@Configuration
public class AppInfoConditionalOnBean {

//    @Bean
//    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(dataSourceProperties.getUrl());
//        config.setUsername(dataSourceProperties.getUsername());
//        config.setPassword(dataSourceProperties.getPassword());
//        config.setDriverClassName(dataSourceProperties.getDriverClassName());
//        return new HikariDataSource(config);
//    }

}
