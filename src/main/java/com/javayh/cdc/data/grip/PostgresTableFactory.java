package com.javayh.cdc.data.grip;

import com.javayh.cdc.data.grip.config.properties.DataGripProperties;
import com.javayh.cdc.data.grip.config.properties.FunctionProperties;
import com.javayh.cdc.data.grip.config.properties.PostgresProperties;
import com.javayh.cdc.data.grip.config.properties.SelectTablesProperties;
import com.javayh.cdc.data.grip.template.DatasourceTemplate;
import com.javayh.cdc.data.grip.template.TableNameArray;
import com.javayh.cdc.data.grip.util.JdbcUtils;
import com.javayh.cdc.data.grip.util.ReaderFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-09
 */
@Slf4j
@Service
public class PostgresTableFactory implements DatasourceTemplate {

    @Autowired
    private DataGripProperties dataGripProperties;
    @Autowired
    private JdbcTemplate template;

    /**
     * 查询数据库所有表名
     *
     * @return {@link TableNameArray}
     */
    @Override
    public List<TableNameArray> queryTableNames() {
        Connection conn = JdbcUtils.getConnection();
        Statement stmt;
        List<TableNameArray> tableNames = new LinkedList<>();
        try {
            stmt = conn.createStatement();
            PostgresProperties postgres = dataGripProperties.getPostgres();
            SelectTablesProperties selectTables = postgres.getSelectTables();
            String name = selectTables.getName();
            ResultSet rs = stmt.executeQuery(name);
            while (rs.next()) {
                String oid = rs.getString(1);
                String tableName = rs.getString(2);
                String comment = rs.getString(3);
                tableNames.add(new TableNameArray(oid, tableName, comment));
            }
            FunctionProperties functions = postgres.getFunctions();
            if (functions.getDdlEnable()) {
                // 创建查询所有便结构的函数，兼容所有postgres 版本
                String functionSql = ReaderFile.getResource(functions.getDdl());
                stmt.executeUpdate(functionSql);
            }
        } catch (SQLException e) {
            log.error("{},{}", e.getErrorCode(), e);
        }
        return tableNames;
    }

    @Override
    public void queryCrateTableDllSql(List<TableNameArray> tableNames) {
        Connection conn = JdbcUtils.getConnection();
        Statement stmt;
        PostgresProperties postgres = dataGripProperties.getPostgres();
        try {
            stmt = conn.createStatement();
            String functionName = postgres.getFunctions().getFunctionName();
            for (TableNameArray o : tableNames) {
                String sql = "SELECT " + functionName + "( '" + o.getTableName() + "' )";
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String crateTabDDl = rs.getString(1);
                }
            }

        } catch (SQLException e) {
            log.error("{},{}", e.getErrorCode(), e);
        }
    }
}
