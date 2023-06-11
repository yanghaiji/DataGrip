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
public class SeqProperties {
    private String selectSeq;
    private String createSeq;
    private String currVal;
    private Boolean enable = true;
}
