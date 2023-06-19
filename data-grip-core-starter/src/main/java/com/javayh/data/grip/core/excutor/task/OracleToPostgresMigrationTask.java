package com.javayh.data.grip.core.excutor.task;

import com.javayh.data.grip.core.configuration.properties.DataGripProperties;
import com.javayh.data.grip.core.excutor.JdbcExecutorService;
import com.javayh.data.grip.core.migration.OracleToPostgresBaseConvertFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * ora to pg task 一个简易版的 任务执行器
 * <p>
 * 需要根据自己的任务量和数据进行相关的调整
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-18
 */
@Slf4j
public class OracleToPostgresMigrationTask implements Runnable {


    /**
     * Oracle 数据库的 JdbcTemplate
     */
    private JdbcTemplate oracleJdbcTemplate;

    /**
     * PostgresSQL 数据库的 JdbcTemplate
     */
    private JdbcTemplate pgsqlJdbcTemplate;

    private DataGripProperties dataGripProperties;

    public OracleToPostgresMigrationTask(JdbcTemplate oracleJdbcTemplate,
                                         JdbcTemplate pgsqlJdbcTemplate,
                                         DataGripProperties dataGripProperties) {
        this.oracleJdbcTemplate = oracleJdbcTemplate;
        this.pgsqlJdbcTemplate = pgsqlJdbcTemplate;
        this.dataGripProperties = dataGripProperties;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        OracleToPostgresBaseConvertFlow oracle = new OracleToPostgresBaseConvertFlow(oracleJdbcTemplate, pgsqlJdbcTemplate, dataGripProperties);
        List<Map<String, Object>> tables = oracle.queryTables();
        CountDownLatch latch = new CountDownLatch(tables.size());
        ThreadPoolExecutor executor = JdbcExecutorService.executor();
        for (Map<String, Object> table : tables) {
            List<String> excludeTables = dataGripProperties.getOracle().getExcludeTables();
            String tableName = (String) table.get("TABLE_NAME");
            executor.execute(() -> {
                if (!excludeTables.contains(tableName)) {
                    // 创建 PostgresSQL 数据库的表
                    oracle.createTable(tableName);
                    // 迁移数据
                    oracle.migrateData(tableName);
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
            oracle.convertView();
            oracle.convertFunction();
        } catch (InterruptedException e) {
            log.error("latch error {}", e.getMessage(), e);
        }
        executor.shutdown();
    }
}
