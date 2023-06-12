package com.javayh.data.grip.jdbc.cdc.controller;

import com.javayh.data.grip.jdbc.cdc.cdc.PostgresJdbcFullDataSyncExec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("pg/")
public class JdbcCdcController {

    @Autowired
    private PostgresJdbcFullDataSyncExec postgresJdbcCDC;

    @GetMapping(value = "sync")
    public void sync() {
        postgresJdbcCDC.sync();
    }
}
