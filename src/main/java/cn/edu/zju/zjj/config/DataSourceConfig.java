package cn.edu.zju.zjj.config;


import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static cn.edu.zju.zjj.config.MybatisConstant.*;

/**
 * bachelor项目数据库的Mybatis 配置类
 * 
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Configuration
@EnableTransactionManagement // 开启事务管理
@MapperScan(basePackages = { "cn.edu.zju.zjj.dao" }, //定义扫描的包名
    sqlSessionFactoryRef = SSF_REF, annotationClass = Repository.class //只扫描Repository注解的Dao
)
public class DataSourceConfig {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(DataSourceConfig.class);

    /**
     * jdbc配置
     */
    private JdbcConfig jdbcConfig;

    @Autowired
    public DataSourceConfig(Environment env) {
        this.jdbcConfig = new JdbcConfig();
        this.jdbcConfig.setDbUrl(env.getProperty("jdbc.url"))
                .setUsername(env.getProperty("jdbc.username"))
                .setPassword(env.getProperty("jdbc.password"))
                .setDriverClassName(env.getProperty("jdbc.driverClassName"))
                .setInitialSize(
                        Integer.parseInt(env.getProperty("jdbc.initialSize")))
                .setMinIdle(Integer.parseInt(env.getProperty("jdbc.minIdle")))
                .setMaxActive(Integer.parseInt(env.getProperty("jdbc.maxActive")))
                .setMaxWait(Integer.parseInt(env.getProperty("jdbc.maxWait")))
                .setTimeBetweenEvictionRunsMillis(Integer.parseInt(
                        env.getProperty("jdbc.timeBetweenEvictionRunsMillis")))
                .setMinEvictableIdleTimeMillis(Integer
                        .parseInt(env.getProperty("jdbc.minEvictableIdleTimeMillis")))
                .setValidationQuery(env.getProperty("jdbc.validationQuery"))
                .setTestWhileIdle(
                        Boolean.parseBoolean(env.getProperty("jdbc.testWhileIdle")))
                .setTestOnBorrow(
                        Boolean.parseBoolean(env.getProperty("jdbc.testOnReturn")))
                .setPoolPreparedStatements(Boolean
                        .parseBoolean(env.getProperty("jdbc.poolPreparedStatements")))
                .setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(env
                        .getProperty("jdbc.maxPoolPreparedStatementPerConnectionSize")))
                .setFilters(env.getProperty("jdbc.filters"))
                .setConnectionProperties(
                        env.getProperty("jdbc.connectionProperties"));
        LOGGER.info("jdbc config {}", jdbcConfig);
    }

    /**
     * 注入DataSource bean
     *
     * @return DataSource
     */
    @Bean(name = DATA_SOURCE)
    @Primary
    public DataSource createDataSource() {
        return Factory.getDataSource(this.jdbcConfig);
    }

    /**
     * 创建sqlSessionFactory
     *
     * @param dataSource
     *            需要dataSource bean
     * @return 创建成功的sqlSessionFactory
     */
    @Bean(SSF_REF)
    @Primary
    public SqlSessionFactory createSqlSessionFactory(
        @Qualifier(DATA_SOURCE) DataSource dataSource) {
        return Factory.createSqlSessionFactory(dataSource, MYBATIS_CONFIG, MAPPER_LOCATIONS);
    }
}
