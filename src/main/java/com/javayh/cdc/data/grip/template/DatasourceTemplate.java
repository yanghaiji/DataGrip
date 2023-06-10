package com.javayh.cdc.data.grip.template;

import java.util.List;

/**
 * <p>
 * 数据同步接口
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-09
 */
public interface DatasourceTemplate {

    /**
     * 查询数据库所有表名
     *
     * @return {@link TableNameArray}
     */
    List<TableNameArray> queryTableNames();


    void queryCrateTableDllSql(List<TableNameArray> tableNames);
}
