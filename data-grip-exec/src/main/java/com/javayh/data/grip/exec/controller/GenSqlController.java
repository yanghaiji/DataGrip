package com.javayh.data.grip.exec.controller;

import com.javayh.data.grip.core.template.TableNameArray;
import com.javayh.data.grip.exec.execute.PostgresDataSourceExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
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
@RestController
@RequestMapping(value = "/gen/")
public class GenSqlController {

    @Autowired
    private PostgresDataSourceExecute createTable;

    @GetMapping(value = "pg")
    public void get() throws SQLException {
        List<TableNameArray> tableNameArrays = createTable.queryTableNamesSql();
        List<String> crateTableDdlSql = createTable.queryCrateTableDdlSql(tableNameArrays);
        List<String> seqDdl = createTable.querySeqDdlSql();
        List<String> indexesDdlSql = createTable.queryIndexesDdlSql();
        List<String> viewDdlSql = createTable.queryViewDdlSql();
        System.out.println("----------------");
    }
}
