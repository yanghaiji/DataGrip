package com.javayh.data.grip.core.configuration.properties;

import com.javayh.data.grip.core.configuration.properties.common.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author haiji
 */
@Data
public class PostgresProperties {

    private String schema;

    /**
     * 为了兼容 pgsql的版本 需要创建函数
     */
    private FunctionProperties functions;

    /**
     * select 相关语句
     */
    private SelectTablesProperties selectTables;

    /**
     * 查询seq的语句
     */
    private SeqProperties sequences;

    /**
     * 查询index的语句
     */
    private IndexProperties indexes;


    /**
     * 查询视图的语句
     */
    private ViewProperties views;

}