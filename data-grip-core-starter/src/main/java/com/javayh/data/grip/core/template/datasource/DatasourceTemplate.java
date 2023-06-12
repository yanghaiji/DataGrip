package com.javayh.data.grip.core.template.datasource;

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
     * 查询所有序列的ddl语句
     *
     * @return {@link List<String>} 创建序列的语句
     */
    List<String> querySeqDdlSql();

    /**
     * 查询数据库所有表名
     *
     * @return {@link TableNameArray}
     */
    List<TableNameArray> queryTableNamesSql();


    /**
     * 根据table names 查询所有的建表语句
     *
     * @param tableNames {@link TableNameArray}
     * @return {@link List<String>} 建表语句的集合
     */
    List<String> queryCrateTableDdlSql(List<TableNameArray> tableNames);

    /**
     * 查询index ddl sql
     *
     * @return {@link List<String>} 创建index的集合
     */
    List<String> queryIndexesDdlSql();

    /**
     * 查询视图ddl sql
     *
     * @return {@link List<String>} 创建视图的集合
     */
    List<String> queryViewDdlSql();

}
