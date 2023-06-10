package com.javayh.cdc.data.grip.config.properties;

import lombok.Data;

/**
 * @author haiji
 */
@Data
public class PostgresProperties {

    private String schema;
    private FunctionProperties functions;
    private SelectTablesProperties selectTables;

}