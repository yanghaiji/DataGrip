package com.javayh.cdc.data.grip.config.properties;

import lombok.Data;

/**
 * @author haiji
 */
@Data
public class OracleProperties {

    private String namespace;
    private FunctionProperties functions;
    private SelectTablesProperties selectTables;

}