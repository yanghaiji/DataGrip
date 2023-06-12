package com.javayh.data.grip.core.template.cdc;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-11
 */
public abstract class JdbcFullDataSyncTemplate {

    /**
     * 查询所有数据库表
     *
     * @return {@link List<String> 数据库表}
     */
    protected abstract List<String> queryTableNames();

    /**
     * 数据同步
     *
     * @param tableNames {@link List<String> 数据库表}
     */
    protected abstract void fullDataSync(List<String> tableNames);


    /**
     * 数据同步
     */
    public void sync() {
        List<String> tableNames = queryTableNames();
        fullDataSync(tableNames);
    }

}
