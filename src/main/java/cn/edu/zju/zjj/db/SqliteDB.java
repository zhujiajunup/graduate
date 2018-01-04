/**
 * @(#)SqliteDB.java, 2018/1/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author 祝佳俊(zhujijunup@163.com)
 */
public class SqliteDB {
    public static void main(String[] args) {
        //连接SQLite的JDBC
        try {
            Class.forName("org.sqlite.JDBC");

            //建立一个数据库名zieckey.db的连接，如果不存在就在当前目录下创建之
            Connection conn = DriverManager
                .getConnection("jdbc:sqlite:db/graduate.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("CREATE TABLE if not exists `weibo_comment`(\n"
                + "`id`  varchar(128) COLLATE NOCASE NOT NULL ,\n"
                + "`user_id`  varchar(128) COLLATE NOCASE NOT NULL ,\n"
                + "`content`  text COLLATE NOCASE ,\n"
                + "`pub_time`  varchar(32) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`source`  varchar(128) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`tweet_id`  varchar(32) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "PRIMARY KEY (`id`)\n" + ")\n" + "ENGINE=InnoDB\n"
                + "DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci\n"
                + "ROW_FORMAT=DYNAMIC\n" + ";\n" + "\n");
            stat.executeUpdate("CREATE TABLE `weibo_tweet` (\n"
                + "`id`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL ,\n"
                + "`time`  varchar(32) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`source`  varchar(64) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`like`  varchar(16) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`comment`  varchar(16) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`transfer`  varchar(16) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`content`  text COLLATE NOCASE NOT NULL ,\n"
                + "`source_tid`  varchar(32) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`flag`  varchar(16) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`uid`  varchar(32) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "PRIMARY KEY (`id`)\n" + ")\n" + "ENGINE=InnoDB\n"
                + "DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci\n"
                + "ROW_FORMAT=DYNAMIC\n" + ";\n" + "\n");
            stat.executeUpdate("CREATE TABLE `weibo_user` (\n"
                + "`id`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL ,\n"
                + "`nickname`  varchar(128) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`tags`  varchar(512) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`gender`  char(8) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`place`  varchar(256) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`signature`  varchar(1024) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`birthday`  varchar(128) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`sex_orientation`  varchar(16) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`edu_info`  varchar(512) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`marriage`  varchar(32) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "`work_info`  varchar(512) COLLATE NOCASE NOT NULL DEFAULT NULL ,\n"
                + "PRIMARY KEY (`id`)\n" + ")\n" + "ENGINE=InnoDB\n"
                + "DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci\n"
                + "ROW_FORMAT=DYNAMIC\n" + ";");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
