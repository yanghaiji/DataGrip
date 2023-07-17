# DataGrip

#### [DataGrip 中文](README_zh.md)

"DataGrip is an open-source program that can export DDL statements such as table creation commands, views, indexes, and sequence for Mysql, Postgres, and Oracle. 
It will also support database schema conversion and data synchronization in the future, and we hope everyone can participate."


# Project structure

```

├───data-grip-core-starter  Core processing package
├───data-grip-exec          Generation of database table structure
├───data-grip-flink-cdc     To be developed
├───data-grip-jdbc-cdc      Data synchronization element
└───doc                     Related document               

```


# Practical exercise

![pgsql_init](doc/img/pgsql_init.png)

- Generate source construction statements, SEQ, view, function and other statements to the project path through `data-grip-exec`
- Import the generated DDL statement into the target database on demand
- Use `data-grip-jdbc-cdc` to synchronize all the source data to the target database, and support the exclusion of tables that do not need to be synchronized


# Implementation Model Translation

## postgres to postgres

![pg_migration](doc/img/pg_migration.png)

Data Transfer through the Following Interfaces Translation
```
### Synchronous build statement
GET http://localhost:8080/datagrip/api/gen/pg

### Synchronous data
GET http://localhost:8081/jdbc/cdc/pg/sync
```


## oracle to postgres

![oracle_to_pgsql](doc/img/oracle_to_pgsql.png)

Data Transfer through the Following Interfaces Translation
```
### orlace sync pgsql
GET http://localhost:8081/jdbc/cdc/ora/pg/sync
```

Currently, Oracle and PostgreSQL only support the synchronization of table structure and data, and do not support the migration of functions.