//package com.javayh.data.grip.flink.cdc.postgres;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import org.apache.commons.lang3.StringUtils;
//import org.postgresql.PGNotification;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author haiji
// */
//public class PostgresSync {
//
//    private static final String SOURCE_URL = "jdbc:postgresql://source:5432/test";
//    private static final String SOURCE_USERNAME = "postgres";
//    private static final String SOURCE_PASSWORD = "postgres";
//
//    private static final String TARGET_URL = "jdbc:postgresql://target:5432/test";
//    private static final String TARGET_USERNAME = "postgres";
//    private static final String TARGET_PASSWORD = "postgres";
//
//    private static final HikariConfig SOURCE_CONFIG = new HikariConfig();
//    private static final HikariConfig TARGET_CONFIG = new HikariConfig();
//
//    private static final String NOTIFY_CHANNEL = "table_update";
//
//    static {
//        SOURCE_CONFIG.setJdbcUrl(SOURCE_URL);
//        SOURCE_CONFIG.setUsername(SOURCE_USERNAME);
//        SOURCE_CONFIG.setPassword(SOURCE_PASSWORD);
//
//        TARGET_CONFIG.setJdbcUrl(TARGET_URL);
//        TARGET_CONFIG.setUsername(TARGET_USERNAME);
//        TARGET_CONFIG.setPassword(TARGET_PASSWORD);
//    }
//
//    private static final JdbcTemplate SOURCE_TEMPLATE = new JdbcTemplate(new HikariDataSource(SOURCE_CONFIG));
//    private static final JdbcTemplate TARGET_TEMPLATE = new JdbcTemplate(new HikariDataSource(TARGET_CONFIG));
//
//    public static void initListener() {
//        String ddlTriggerSql = "CREATE OR REPLACE FUNCTION notify_ddl() " +
//                "RETURNS EVENT_TRIGGER AS " +
//                "$BODY$ " +
//                "DECLARE " +
//                "    stmt text; " +
//                "BEGIN " +
//                "    stmt := format('SELECT pg_notify(%L, %L)', '%s', current_user); " +
//                "    EXECUTE stmt; " +
//                "END; " +
//                "$BODY$ " +
//                "LANGUAGE plpgsql; " +
//
//                "DROP EVENT TRIGGER IF EXISTS on_alter_table CASCADE; " +
//
//                "CREATE EVENT TRIGGER on_alter_table " +
//                "ON ddl_command_end " +
//                "WHEN tag IN ('ALTER TABLE ADD COLUMN', 'ALTER TABLE ALTER COLUMN TYPE', 'ALTER TABLE DROP COLUMN', 'ALTER TABLE RENAME COLUMN') " +
//                "EXECUTE FUNCTION notify_ddl(); ";
//
//        String listenerSql = "LISTEN " + NOTIFY_CHANNEL + ";";
//
//        SOURCE_TEMPLATE.execute(ddlTriggerSql);
//        SOURCE_TEMPLATE.execute(listenerSql);
//    }
//
//    public static void sync(String tableName, boolean incrementalSync) throws SQLException {
//
//        String primaryKey = getPrimaryKey(tableName);
//
//        if (!incrementalSync) {
//            truncateTable(tableName);
//        }
//
//        String selectSql = "SELECT * FROM " + tableName;
//        if (incrementalSync) {
//            String timestampColumn = getTimestampColumn(tableName);
//            if (StringUtils.isNotEmpty(timestampColumn)) {
//                String lastSyncTime = getLastSyncTime(tableName);
//                selectSql = selectSql + " WHERE " + timestampColumn + " > '" + lastSyncTime + "'";
//            }
//        }
//
//        List<Map<String, Object>> sourceData = SOURCE_TEMPLATE.queryForList(selectSql);
//
//        List<String> columns = new ArrayList<>();
//        StringBuffer insertSql = new StringBuffer();
//        insertSql.append("INSERT INTO " + tableName + " (");
//
//        for (String key : sourceData.get(0).keySet()) {
//            columns.add(key);
//            insertSql.append(key + ", ");
//        }
//
//        insertSql.deleteCharAt(insertSql.length() - 1);
//        insertSql.deleteCharAt(insertSql.length() - 1);
//        insertSql.append(") VALUES (");
//
//        for (int i = 0; i < columns.size(); i++) {
//            insertSql.append("?, ");
//        }
//
//        insertSql.deleteCharAt(insertSql.length() - 1);
//        insertSql.deleteCharAt(insertSql.length() - 1);
//        insertSql.append(")");
//
//        PreparedStatement insertStatement = TARGET_TEMPLATE.getDataSource().getConnection().prepareStatement(insertSql.toString());
//
//        for (Map<String, Object> row : sourceData) {
//            int i = 1;
//            for (String column : columns) {
//                Object value = row.get(column);
//                if (value == null) {
//                    insertStatement.setNull(i, Types.NULL);
//                } else if (value instanceof Integer) {
//                    insertStatement.setInt(i, (Integer) value);
//                } else if (value instanceof Long) {
//                    insertStatement.setLong(i, (Long) value);
//                } else if (value instanceof Float) {
//                    insertStatement.setFloat(i, (Float) value);
//                } else if (value instanceof Double) {
//                    insertStatement.setDouble(i, (Double) value);
//                } else if (value instanceof Boolean) {
//                    insertStatement.setBoolean(i, (Boolean) value);
//                } else if (value instanceof Date) {
//                    insertStatement.setTimestamp(i, new Timestamp(((Date) value).getTime()));
//                } else {
//                    insertStatement.setString(i, value.toString());
//                }
//                i++;
//            }
//            insertStatement.execute();
//        }
//
//        if (incrementalSync) {
//            updateSyncTime(tableName);
//        }
//    }
//
//    private static String getPrimaryKey(String tableName) throws SQLException {
//        String sql = "SELECT a.attname " +
//                "FROM   pg_index i " +
//                "   JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = ANY(i.indkey) " +
//                "WHERE  i.indrelid = ?::regclass AND i.indisprimary;";
//
//        return SOURCE_TEMPLATE.queryForObject(sql, String.class, tableName);
//    }
//
//    private static void truncateTable(String tableName) {
//        TARGET_TEMPLATE.execute("TRUNCATE TABLE " + tableName);
//    }
//
//    private static String getTimestampColumn(String tableName) {
//        String sql = "SELECT column_name " +
//                "FROM information_schema.columns " +
//                "WHERE table_name = ? " +
//                "AND column_name LIKE 'timestamp%' " +
//                "ORDER BY ordinal_position " +
//                "LIMIT 1;";
//        return SOURCE_TEMPLATE.queryForObject(sql, String.class, tableName);
//    }
//
//    private static String getLastSyncTime(String tableName) {
//        String sql = "SELECT last_sync_time FROM metadata WHERE table_name = ?";
//        String lastSyncTime = TARGET_TEMPLATE.queryForObject(sql, String.class, tableName);
//        return lastSyncTime == null ? "1970-01-01 00:00:00" : lastSyncTime;
//    }
//
//    private static void updateSyncTime(String tableName) {
//        String sql = "INSERT INTO metadata (table_name, last_sync_time) VALUES (?, ?) " +
//                "ON CONFLICT (table_name) DO UPDATE SET last_sync_time = ?";
//        TARGET_TEMPLATE.update(sql, tableName, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
//    }
//
//    public static void main(String[] args) throws SQLException, InterruptedException {
//
//        PostgresSync.initListener();
//
//        Thread listenerThread = new Thread(() -> {
//            while (true) {
//                Connection conn = null;
//                try {
//                    conn = SOURCE_TEMPLATE.getDataSource().getConnection();
//                    PGNotification[] notifications = new PGNotification(conn);
//
//                    if (notifications != null && notifications.length > 0) {
//                        for (PGNotification notification : notifications) {
//                            String tableName = notification.getParameter();
//                            System.out.println("Received notification for table: " + tableName);
//                            try {
//                                sync(tableName, true);
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    Thread.sleep(100);
//                } catch (SQLException | InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (conn != null) {
//                        try {
//                            conn.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//
//        listenerThread.start();
//        listenerThread.join();
//    }
//}
