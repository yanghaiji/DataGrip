package com.javayh.cdc.data.grip.util;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC Utils 工具类
 *
 * @author haiji
 */
@Component
public class JdbcUtils implements InitializingBean {

    private final HikariDataSource dataSource;
    private static JdbcUtils jdbcUtils;

    @Autowired
    public JdbcUtils(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = jdbcUtils.dataSource.getConnection();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return connection;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcUtils = this;
    }
}


