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
     * 有权限的 owner ,必填字段，否则会异常
     */
    private String user;

    /**
     * 排除不同步的表
     */
    private List<String> excludeTables;

    private CustomFunctionProperties customFunctions;
    private SelectTablesProperties selectTables;

}