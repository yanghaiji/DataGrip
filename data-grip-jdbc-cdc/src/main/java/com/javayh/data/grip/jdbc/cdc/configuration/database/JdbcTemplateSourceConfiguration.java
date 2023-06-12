package com.javayh.data.grip.jdbc.cdc.configuration.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-11
 */
@Configuration
public class JdbcTemplateSourceConfiguration {

    @Bean("targetJdbcTemplate")
    public JdbcTemplate storyJdbcTemplate(@Qualifier(value = "targetDatasource") HikariDataSource targetDatasource) {
        return new JdbcTemplate(targetDatasource);
    }

    @Bean("sourceJdbcTemplate")
    public JdbcTemplate testJdbcTemplate(@Qualifier(value = "sourceDatasource") HikariDataSource sourceDatasource) {
        return new JdbcTemplate(sourceDatasource);
    }
}
