/*
 * 下面的SQL完成下面的操作：
 * 1) 新建数据库
 * 2) 新建用户，并把新建的数据库的所有权限赋给该用户
 *
 * 要用MySQL的root用户来执行这些操作。
 */

CREATE DATABASE doris_config CHARACTER SET UTF8;
GRANT ALL PRIVILEGES ON doris_config.* TO doris@"%" identified by "doris";