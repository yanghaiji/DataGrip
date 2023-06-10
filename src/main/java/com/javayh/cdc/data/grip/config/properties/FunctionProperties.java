package com.javayh.cdc.data.grip.config.properties;

import lombok.Data;

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
public class FunctionProperties {

    private String functionName;
    private String ddl;
    private Boolean ddlEnable;

}
