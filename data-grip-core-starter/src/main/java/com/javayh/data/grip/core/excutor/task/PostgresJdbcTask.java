package com.javayh.data.grip.core.excutor.task;

import cn.hutool.json.JSONObject;
import com.javayh.data.grip.core.configuration.properties.DataGripProperties;
import com.javayh.data.grip.core.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * jdbc cdc task
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-11
 */
@Slf4j
public class PostgresJdbcTask implements Runnable {

    private final List<String> tableNames;
    private final JdbcTemplate sourceJdbcTemplate;
    private final JdbcTemplate targetJdbcTemplate;
    private final DataGripProperties dataGripProperties;
    private final CountDownLatch latch;
    private final ThreadPoolExecutor executor;

    public PostgresJdbcTask(List<String> tableNames, JdbcTemplate sourceJdbcTemplate,
                            JdbcTemplate targetJdbcTemplate, DataGripProperties dataGripProperties,
                            CountDownLatch latch, ThreadPoolExecutor executor) {
        this.tableNames = tableNames;
        this.targetJdbcTemplate = targetJdbcTemplate;
        this.sourceJdbcTemplate = sourceJdbcTemplate;
        this.dataGripProperties = dataGripProperties;
        this.latch = latch;
        this.executor = executor;
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
        int pageSize = dataGripProperties.getPostgres().getPageSize();
        List<String> excludeTables = dataGripProperties.getPostgres().getExcludeTables();
        for (String tableName : tableNames) {
            if (excludeTables.contains(tableName)) {
                // 跳过无效同步的表
                continue;
            }
            executor.execute(() -> {
                AtomicInteger offset = new AtomicInteger(0);
                Integer totalRowCount = sourceJdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName + ";", Integer.class);
                try {
                    // 分页查询次数
                    int pageCount = (totalRowCount + pageSize - 1) / pageSize;
                    // 分页查询数据并批量插入目标数据库
                    for (int pageNum = 1; pageNum <= pageCount; pageNum++) {
                        offset.set((pageNum - 1) * pageSize);
                        List<Map<String, Object>> rows = sourceJdbcTemplate.queryForList("SELECT * FROM " + tableName + " LIMIT " + pageSize + " OFFSET " + offset.get());
                        String insertSql = "INSERT INTO " + tableName + " (" + StringUtils.join(rows.get(0).keySet(), ", ") + ")" +
                                " VALUES (" + StringUtils.repeat("?", ", ", rows.get(0).size()) + ")";
                        targetJdbcTemplate.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                Map<String, Object> row = rows.get(i);
                                int idx = 1;
                                for (Object value : row.values()) {
                                    ps.setObject(idx++, value);
                                }
                            }

                            @Override
                            public int getBatchSize() {
                                log.debug("tableName = {} , totalRowCount = {} pageCount = {}, ,offset = {}", tableName, totalRowCount, pageCount, offset.get());
                                return rows.size();
                            }
                        });
                    }
                    log.info("tableName  {} 同步完成 ", tableName);
                } catch (Exception e) {
                    log.error("数据同步失败，tableName  {},{}", tableName, e);
                    JSONObject entries = new JSONObject();
                    entries.append("tableName", tableName)
                            .append("totalRowCount", totalRowCount)
                            .append("offset", offset.get());
                    FileUtils.appendUtf8Lines(entries.toString());
                } finally {
                    latch.countDown();
                }
            });
        }
    }
}
