package com.javayh.data.grip.core.configuration.properties.common;

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
    private String selectFunctionName;
    private String functionDdl;
    private Boolean ddlEnable;
}
