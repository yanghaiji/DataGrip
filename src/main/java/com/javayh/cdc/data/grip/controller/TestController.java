package com.javayh.cdc.data.grip.controller;

import com.javayh.cdc.data.grip.PostgresTableFactory;
import com.javayh.cdc.data.grip.template.TableNameArray;
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
@RequestMapping(value = "/test/")
public class TestController {

    @Autowired
    private PostgresTableFactory createTable;

    @GetMapping(value = "get")
    public void get() throws SQLException {
        List<TableNameArray> tableNameArrays = createTable.queryTableNames();
        createTable.queryCrateTableDllSql(tableNameArrays);
    }
}
