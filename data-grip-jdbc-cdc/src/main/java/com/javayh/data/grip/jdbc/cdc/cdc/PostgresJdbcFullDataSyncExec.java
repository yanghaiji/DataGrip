package com.javayh.data.grip.jdbc.cdc.cdc;


import com.javayh.data.grip.core.configuration.properties.DataGripProperties;
import com.javayh.data.grip.core.excutor.JdbcExecutorService;
import com.javayh.data.grip.core.excutor.task.PostgresJdbcTask;
import com.javayh.data.grip.core.template.cdc.JdbcFullDataSyncTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-11
 */
@Slf4j
@Service
public class PostgresJdbcFullDataSyncExec extends JdbcFullDataSyncTemplate {

    public static final String SELECT_TABLE_NAME_FROM_INFORMATION_SCHEMA_TABLES_WHERE_TABLE_SCHEMA_PUBLIC_AND_TABLE_TYPE_BASE_TABLE = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public_ns' AND table_type = 'BASE TABLE';";

    private final JdbcTemplate targetJdbcTemplate;
    private final JdbcTemplate sourceJdbcTemplate;
    private final DataGripProperties dataGripProperties;

    public PostgresJdbcFullDataSyncExec(@Qualifier("targetJdbcTemplate") JdbcTemplate targetJdbcTemplate,
                                        @Qualifier("sourceJdbcTemplate") JdbcTemplate sourceJdbcTemplate,
                                        DataGripProperties dataGripProperties) {
        this.targetJdbcTemplate = targetJdbcTemplate;
        this.sourceJdbcTemplate = sourceJdbcTemplate;
        this.dataGripProperties = dataGripProperties;
    }

    /**
     * 查询所有数据库表
     *
     * @return {@link List <String> 数据库表}
     */
    @Override
    protected List<String> queryTableNames() {
        String schema = dataGripProperties.getPostgres().getSchema();
        // 获取源数据库中所有表的表名
        return sourceJdbcTemplate.queryForList(
                SELECT_TABLE_NAME_FROM_INFORMATION_SCHEMA_TABLES_WHERE_TABLE_SCHEMA_PUBLIC_AND_TABLE_TYPE_BASE_TABLE.replace("public_ns", schema),
                String.class);
    }

    /**
     * 数据同步
     *
     * @param tableNames {@link List<String> 数据库表}
     */
    @Override
    protected void fullDataSync(List<String> tableNames) {
        ThreadPoolExecutor executor = JdbcExecutorService.executor();
        CountDownLatch latch = new CountDownLatch(tableNames.size());
        // 遍历每个表，查询数据并插入到目标数据库中对应的表中
        executor.execute(new PostgresJdbcTask(tableNames, sourceJdbcTemplate, targetJdbcTemplate, dataGripProperties, latch, executor));
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("数据同步全部完成,可以进行数据对比");
        executor.shutdown();

    }
}
