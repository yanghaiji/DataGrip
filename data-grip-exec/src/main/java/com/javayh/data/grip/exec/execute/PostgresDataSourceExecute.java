package com.javayh.data.grip.exec.execute;

import com.javayh.data.grip.core.configuration.properties.DataGripProperties;
import com.javayh.data.grip.core.configuration.properties.PostgresProperties;
import com.javayh.data.grip.core.configuration.properties.common.FunctionProperties;
import com.javayh.data.grip.core.configuration.properties.common.SelectTablesProperties;
import com.javayh.data.grip.core.configuration.properties.common.SeqProperties;
import com.javayh.data.grip.core.configuration.properties.common.ViewProperties;
import com.javayh.data.grip.core.exception.GenDdlException;
import com.javayh.data.grip.core.template.DatasourceTemplate;
import com.javayh.data.grip.core.template.TableNameArray;
import com.javayh.data.grip.core.util.JdbcUtils;
import com.javayh.data.grip.core.util.ReaderFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PostgresDataSourceExecute implements DatasourceTemplate {

    @Autowired
    private DataGripProperties dataGripProperties;


    /**
     * 查询所有序列的ddl语句
     *
     * @return {@link List<String>} 创建序列的语句
     */
    @Override
    public List<String> querySeqDdlSql() {
        Connection conn = JdbcUtils.getConnection();
        Statement stmt;
        List<String> createSeqSqlList = new LinkedList<>();
        try {
            SeqProperties sequences = dataGripProperties.getPostgres().getSequences();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sequences.getSelectSeq());
            while (rs.next()) {
                // sequence_name,start_value,minimum_value,maximum_value,increment
                String sequenceName = rs.getString("sequence_name");
                String startValue = rs.getString("start_value");
                String minimumValue = rs.getString("minimum_value");
                String maximumValue = rs.getString("maximum_value");
                String increment = rs.getString("increment");
                ResultSet resultSet = stmt.executeQuery(sequences.getCurrVal().replace("currValue", sequenceName));
                while (resultSet.next()) {
                    startValue = resultSet.getString("nextval");
                }
                String createSeqSql = sequences.getCreateSeq().replace("sequence_name", sequenceName)
                        .replace("start_value", startValue)
                        .replace("minimum_value", minimumValue)
                        .replace("maximum_value", maximumValue)
                        .replace("increment_by", increment);
                createSeqSqlList.add(createSeqSql);
            }
        } catch (SQLException e) {
            log.error("querySeqDdl {},{}", e.getErrorCode(), e);
            throw new GenDdlException(e);
        }
        return createSeqSqlList;
    }

    /**
     * 查询数据库所有表名
     *
     * @return {@link TableNameArray}
     */
    @Override
    public List<TableNameArray> queryTableNamesSql() {
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

    /**
     * 根据table names 查询所有的建表语句
     *
     * @param tableNames {@link TableNameArray}
     * @return {@link List<String>} 建表语句的集合
     */
    @Override
    public List<String> queryCrateTableDdlSql(List<TableNameArray> tableNames) {
        Connection conn = JdbcUtils.getConnection();
        Statement stmt;
        PostgresProperties postgres = dataGripProperties.getPostgres();
        List<String> tableDdl = new LinkedList<>();
        try {
            stmt = conn.createStatement();
            String functionName = postgres.getFunctions().getFunctionName();
            for (TableNameArray o : tableNames) {
                String sql = "SELECT " + functionName + "( '" + o.getTableName() + "' )";
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String crateTabDDl = rs.getString(1);
                    tableDdl.add(crateTabDDl);
                }
            }
            return tableDdl;
        } catch (SQLException e) {
            log.error("{},{}", e.getErrorCode(), e);
            throw new GenDdlException(e);
        }
    }

    /**
     * 查询index ddl sql
     *
     * @return {@link List<String>} 创建index的集合
     */
    @Override
    public List<String> queryIndexesDdlSql() {
        Connection conn = JdbcUtils.getConnection();
        Statement stmt;
        PostgresProperties postgres = dataGripProperties.getPostgres();
        List<String> indexDdlSql = new LinkedList<>();
        try {
            stmt = conn.createStatement();
            String selectIndex = postgres.getIndexes().getSelectIndex();
            ResultSet rs = stmt.executeQuery(selectIndex);
            while (rs.next()) {
                // 获取索引的名字
                //String indexname = rs.getString("indexname");
                // 获取创建索引的语句
                String ddlIndexSql = rs.getString("indexdef");
                indexDdlSql.add(ddlIndexSql);
            }
            return indexDdlSql;
        } catch (SQLException e) {
            log.error("{},{}", e.getErrorCode(), e);
            throw new GenDdlException(e);
        }
    }

    /**
     * 查询视图ddl sql
     *
     * @return {@link List<String>} 创建视图的集合
     */
    @Override
    public List<String> queryViewDdlSql() {
        Connection conn = JdbcUtils.getConnection();
        Statement stmt;
        PostgresProperties postgres = dataGripProperties.getPostgres();
        List<String> viewDdlSql = new LinkedList<>();
        try {
            stmt = conn.createStatement();
            ViewProperties views = postgres.getViews();
            String selectViewName = views.getSelectViewName();
            ResultSet rs = stmt.executeQuery(selectViewName);
            while (rs.next()) {
                String viewName = rs.getString("viewname");
                String definition = rs.getString("definition");
                String viewCreationStatement = String.format("CREATE VIEW %s AS %s", viewName, definition);
                viewDdlSql.add(viewCreationStatement);
            }
            return viewDdlSql;
        } catch (SQLException e) {
            log.error("{},{}", e.getErrorCode(), e);
            throw new GenDdlException(e);
        }
    }
}
