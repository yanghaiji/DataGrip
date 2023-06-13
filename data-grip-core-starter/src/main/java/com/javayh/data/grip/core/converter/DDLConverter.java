package com.javayh.data.grip.core.converter;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 数据库架构转换
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-12
 */
public class DDLConverter {

    public static final String REGEX = "nextval\\([^']*'[^']*'::regclass\\)";

    /**
     * 数据转换工具
     *
     * @param sqlDialect {@link SQLDialect}
     * @return {@link  List<String>} 所有的建表语句
     */
    public static List<String> convertDDL(List<String> ds, SQLDialect sqlDialect) {
        List<String> ddlSql = new LinkedList<>();
        // Create a jOOQ DSLContext
        DSLContext ctx = DSL.using(sqlDialect, new Settings());
        ds.forEach(ddl -> {
            Query query = ctx.parser().parse(ddl).queries()[0];
            ddlSql.add(ctx.render(query));
        });
        return ddlSql;
    }


}
