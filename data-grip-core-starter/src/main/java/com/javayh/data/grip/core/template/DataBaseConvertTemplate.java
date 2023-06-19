package com.javayh.data.grip.core.template;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-16
 */
public abstract class DataBaseConvertTemplate {

    /**
     * 查询所有的表
     *
     * @return {@link List<Map<String, Object>> 数据所有的表}
     */
    protected abstract List<Map<String, Object>> queryTables();

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


    /**
     * 视图转换
     */
    protected abstract void convertView();

    /**
     * 函数转换
     */
    protected abstract void convertFunction();


}
