package com.javayh.data.grip.core.template;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-16
 */
public abstract class DataMigrationTemplate {

    /**
     * 转换创建数据库
     *
     * @param tableName 数据库表名
     */
    protected abstract void createTable(String tableName);

    /**
     * 开始迁移数据
     *
     * @param tableName 需要迁移的数据的表名
     */
    protected abstract void migrateData(String tableName);


}
