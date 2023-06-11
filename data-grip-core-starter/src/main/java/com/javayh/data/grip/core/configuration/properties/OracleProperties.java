package com.javayh.data.grip.core.configuration.properties;


import com.javayh.data.grip.core.configuration.properties.common.FunctionProperties;
import com.javayh.data.grip.core.configuration.properties.common.SelectTablesProperties;
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