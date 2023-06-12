package com.javayh.data.grip.exec.controller;

import com.javayh.data.grip.core.template.datasource.TableNameArray;
import com.javayh.data.grip.core.util.FileUtils;
import com.javayh.data.grip.exec.execute.PostgresDataSourceExecute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.Duration;
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
@RestController
@RequestMapping(value = "/gen/")
public class GenSqlController {

    @Autowired
    private PostgresDataSourceExecute createTable;

    @GetMapping(value = "pg")
    public void get() throws SQLException {
        long startTime = System.nanoTime();
        List<TableNameArray> tableNameArrays = createTable.queryTableNamesSql();
        List<String> crateTableDdlSql = createTable.queryCrateTableDdlSql(tableNameArrays);
        List<String> seqDdl = createTable.querySeqDdlSql();
        List<String> indexesDdlSql = createTable.queryIndexesDdlSql();
        List<String> viewDdlSql = createTable.queryViewDdlSql();
        List<String> functionDdlSql = createTable.queryFunctionDdlSql();
        FileUtils.writeLines(indexesDdlSql, "PostgresIndexesDdlSql.sql");
        FileUtils.writeLines(viewDdlSql, "PostgresViewDdlSql.sql");
        FileUtils.writeLines(crateTableDdlSql, "PostgresCrateTableDdlSql.sql");
        FileUtils.writeLines(seqDdl, "PostgresSeqDdl.sql");
        FileUtils.writeLines(functionDdlSql, "PostgresFunctionsDdl.sql");
        Duration timeTakenToStartup = Duration.ofNanos(System.nanoTime() - startTime);
        log.info("Postgres database structure synchronization is complete ,{}", timeTakenToStartup);
    }
}
