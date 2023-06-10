package com.javayh.cdc.data.grip.config.properties;


import lombok.Data;

/**
 * @author haiji
 */
@Data
public class MysqlProperties {

    private String schema;
    private SelectTablesProperties selectTables;
    private FunctionProperties functions;

}