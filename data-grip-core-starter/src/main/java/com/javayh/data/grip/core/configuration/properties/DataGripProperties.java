package com.javayh.data.grip.core.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-10
 */
@Data
@ConfigurationProperties(prefix = "data.grip")
public class DataGripProperties {
    /**
     * postgres sql 配置
     */
    private PostgresProperties postgres;

    /**
     * mysql 配置
     */
    private MysqlProperties mysql;

    /**
     * oracle 配置
     */
    private OracleProperties oracle;
}
