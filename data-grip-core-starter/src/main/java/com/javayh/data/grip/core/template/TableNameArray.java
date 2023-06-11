package com.javayh.data.grip.core.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 数据库表名
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableNameArray {
    private String oid;
    private String tableName;
    private String comment;
}
