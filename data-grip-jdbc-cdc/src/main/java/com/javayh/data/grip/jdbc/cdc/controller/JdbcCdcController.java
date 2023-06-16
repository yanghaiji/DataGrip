package com.javayh.data.grip.jdbc.cdc.controller;

import com.javayh.data.grip.core.configuration.properties.DataGripProperties;
import com.javayh.data.grip.core.migration.OracleToPostgresMigration;
import com.javayh.data.grip.jdbc.cdc.cdc.PostgresJdbcFullDataSyncExec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-11
 */
@RestController
public class JdbcCdcController {

    @Autowired
    private PostgresJdbcFullDataSyncExec postgresJdbcCDC;
    @Autowired
    private JdbcTemplate targetJdbcTemplate;
    @Autowired
    private JdbcTemplate sourceJdbcTemplate;
    @Autowired
    private DataGripProperties dataGripProperties;


    @GetMapping(value = "pg/sync")
    public void sync() {
        postgresJdbcCDC.sync();
    }

    @GetMapping(value = "ora/pg/sync")
    public void oraPgSync() throws SQLException {
        new OracleToPostgresMigration(sourceJdbcTemplate, targetJdbcTemplate, dataGripProperties).dataMigration();
    }
}
