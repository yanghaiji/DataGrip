# DataGrip

`DataGrip` 是一款，可以导出`Mysql`、`Postgres` 、`Oracle` 建表语句，视图，索引以及序列等DDL语句的开源程序

未来将支持数据库架构转换，以及数据同步等特性，希望大家一起参加

# 项目架构

```

├───data-grip-core-starter  核心处理公共包
├───data-grip-exec          数据库表结构的生成
├───data-grip-flink-cdc     待开发
├───data-grip-jdbc-cdc      数据同步组件
└───doc                     相关文档               

```


# 实战演练

![pgsql_init](doc/img/pgsql_init.png)

- 通过`data-grip-exec` 将source的建表语句、SEQ、view、function等语句生成到项目路径下
- 将生成的的DDL语句，按需导入到target数据库
- 通过`data-grip-jdbc-cdc`将source的数据全量同步到target的数据库中，支持排除无需同步的表

