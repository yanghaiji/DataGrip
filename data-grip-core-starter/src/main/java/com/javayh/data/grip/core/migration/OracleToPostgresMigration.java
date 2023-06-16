package com.javayh.data.grip.core.migration;

import com.javayh.data.grip.core.configuration.properties.DataGripProperties;
import com.javayh.data.grip.core.configuration.properties.OracleProperties;
import com.javayh.data.grip.core.db.DbTypes;
import com.javayh.data.grip.core.template.DataMigrationTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * oracle 数据传输到postgres中
 *
 * @author haiji
 */
@Slf4j
public class OracleToPostgresMigration extends DataMigrationTemplate {

    /**
     * Oracle 数据库的 JdbcTemplate
     */
    private JdbcTemplate oracleJdbcTemplate;

    /**
     * PostgresSQL 数据库的 JdbcTemplate
     */
    private JdbcTemplate pgsqlJdbcTemplate;

    private DataGripProperties dataGripProperties;

    public OracleToPostgresMigration(JdbcTemplate oracleJdbcTemplate,
                                     JdbcTemplate pgsqlJdbcTemplate,
                                     DataGripProperties dataGripProperties) {
        this.oracleJdbcTemplate = oracleJdbcTemplate;
        this.pgsqlJdbcTemplate = pgsqlJdbcTemplate;
        this.dataGripProperties = dataGripProperties;
    }

    /**
     * 数据转出管道
     */
    public void dataMigration() {
        // 获取 Oracle 数据库中的表信息（表名、字段名、数据类型等）
        List<Map<String, Object>> tables = oracleJdbcTemplate.queryForList("SELECT * FROM USER_TABLES");
        for (Map<String, Object> table : tables) {
            List<String> excludeTables = dataGripProperties.getOracle().getExcludeTables();
            String tableName = (String) table.get("TABLE_NAME");
            if (!excludeTables.contains(tableName)) {
                // 创建 PostgresSQL 数据库的表
                createTable(tableName);
                // 迁移数据
                migrateData(tableName);
            }
        }
    }

    /**
     * 创建 PostgreSQL 数据库的表
     *
     * @param tableName 数据库表
     * @return ddl sql
     */
    @Override
    protected void createTable(String tableName) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(tableName).append("(");
        // 查询 Oracle 数据库中表的字段信息
        List<Map<String, Object>> columns = oracleJdbcTemplate.queryForList("SELECT * FROM USER_TAB_COLUMNS WHERE TABLE_NAME = ?", tableName);
        for (Map<String, Object> column : columns) {
            String columnName = (String) column.get("COLUMN_NAME");
            String dataType = (String) column.get("DATA_TYPE");
            int dataLength = ((Number) column.get("DATA_LENGTH")).intValue();
            // 根据 Oracle 数据库的数据类型转换为 Postgres 数据库的数据类型
            String pgDataType;
            if (dataType.equalsIgnoreCase(DbTypes.VARCHAR2.name())
                    || dataType.equalsIgnoreCase(DbTypes.NVARCHAR2.name())
                    || dataType.equalsIgnoreCase(DbTypes.CHAR.name())) {
                pgDataType = DbTypes.VARCHAR.name().concat("(" + dataLength + ")");
            } else if (dataType.equalsIgnoreCase(DbTypes.NUMBER.name())
                    || dataType.equalsIgnoreCase(DbTypes.FLOAT.name())
                    || dataType.equalsIgnoreCase(DbTypes.DECIMAL.name())) {
                Object data_precision = column.get("DATA_PRECISION");
                Object data_scale = column.get("DATA_SCALE");
                if (Objects.nonNull(data_scale) && Objects.nonNull(data_precision)) {
                    int precision = ((Number) data_precision).intValue();
                    int scale = ((Number) data_scale).intValue();
                    if (scale <= 0) {
                        pgDataType = "NUMERIC(" + precision + ")";
                    } else {
                        pgDataType = "NUMERIC(" + precision + "," + scale + ")";
                    }
                } else {
                    pgDataType = DbTypes.NUMERIC.name();
                }
            } else if (dataType.equalsIgnoreCase(DbTypes.DATE.name())
                    || dataType.equalsIgnoreCase(DbTypes.TIMESTAMP.name())
                    || dataType.contains(DbTypes.TIMESTAMP.name())) {
                pgDataType = DbTypes.TIMESTAMP.name();
            } else {
                throw new RuntimeException("Unsupported data type: " + dataType);
            }
            sql.append(columnName).append(" ").append(pgDataType).append(",");
        }
        // 去除最后一个逗号
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        // 执行创建 PostgreSQL 表的 SQL 语句
        pgsqlJdbcTemplate.execute(sql.toString());
    }


    /**
     * 迁移数据
     *
     * @param tableName {@link String} 数据库表名
     */
    @Override
    protected void migrateData(String tableName) {
        int page = 0;
        boolean hasNextPage = true;
        // 假设每页查询1000条数据
        int pageSize = dataGripProperties.getOracle().getPageSize();
        while (hasNextPage) {
            // 分页查询 Oracle 数据库中的数据
            List<Map<String, Object>> data = oracleJdbcTemplate.queryForList(getPagingSql(tableName, page, pageSize));
            if (data.isEmpty()) {
                hasNextPage = false;
            } else {
                // 排除 RN : 由于 oracle 查询是带出来的行号，但是在真是的数据传输时不需要这个行号，需要剔除
                Set<String> fields = data.get(0).keySet();
                String insertSql = "INSERT INTO " + tableName + " (" +
                        StringUtils.join(fields.stream().filter(o -> !"RN".equals(o)).collect(Collectors.toList()), ", ") + ")" +
                        " VALUES (" + StringUtils.repeat("?", ", ", fields.size() - 1) + ")";
                pgsqlJdbcTemplate.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Map<String, Object> row = data.get(i);
                        row.remove("RN");
                        int idx = 1;
                        for (Object value : row.values()) {
                            ps.setObject(idx++, value);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return data.size();
                    }
                });

            }
            page++;
        }
    }

    /**
     * 分批次查询
     *
     * @param tableName 要查询的表
     * @param page      每次查询的页数
     * @param pageSize  默认 1000 {@link OracleProperties#getPageSize()}
     * @return 返回
     */
    private String getPagingSql(String tableName, int page, int pageSize) {
        int start = page * pageSize + 1;
        int end = (page + 1) * pageSize;
        return "SELECT * FROM (SELECT ROWNUM rn, t.* FROM " + tableName + " t WHERE ROWNUM <= " + end + ") WHERE rn >= " + start;
    }


}