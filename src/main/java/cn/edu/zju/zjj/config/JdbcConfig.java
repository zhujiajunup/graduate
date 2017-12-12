/**
 * @(#)Config.java, 2017/10/12.
 * <p/>
 * Copyright 2017 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Setter
@Getter
@ToString(of = { "dbUrl", "username", "password", "driverClassName" })
@Accessors(chain = true)
class JdbcConfig {

    private String dbUrl;

    private String username;

    private String password;

    private String driverClassName;

    private int initialSize;

    private int minIdle;

    private int maxActive;

    private int maxWait;

    private int timeBetweenEvictionRunsMillis;

    private int minEvictableIdleTimeMillis;

    private String validationQuery;

    private boolean testWhileIdle;

    private boolean testOnBorrow;

    private boolean testOnReturn;

    private boolean poolPreparedStatements;

    private int maxPoolPreparedStatementPerConnectionSize;

    private String filters;

    private String connectionProperties;

}
