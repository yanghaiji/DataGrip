package com.javayh.data.grip.core.configuration.properties;


import com.javayh.data.grip.core.configuration.properties.common.CustomFunctionProperties;
import com.javayh.data.grip.core.configuration.properties.common.SelectTablesProperties;
import lombok.Data;

import java.util.List;

/**
 * @author haiji
 */
@Data
public class OracleProperties {

    private String namespace;
    /**
     * 每页的数据
     */
    private Integer pageSize = 1000;

    /**
     * 排除不同步的表
     */
    private List<String> excludeTables;

    private CustomFunctionProperties customFunctions;
    private SelectTablesProperties selectTables;

}