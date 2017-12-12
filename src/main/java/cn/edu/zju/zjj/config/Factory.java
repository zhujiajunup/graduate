package cn.edu.zju.zjj.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * data source factory
 * 
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
class Factory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);

    /**
     * 获取DataSource实例
     * 
     * @param jdbcConfig
     *            jdbc 配置
     * @return DataSource instance
     */
    static DataSource getDataSource(JdbcConfig jdbcConfig) {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(jdbcConfig.getDbUrl());
        datasource.setUsername(jdbcConfig.getUsername());
        datasource.setPassword(jdbcConfig.getPassword());
        datasource.setDriverClassName(jdbcConfig.getDriverClassName());
        //configuration
        datasource.setMinIdle(jdbcConfig.getMinIdle());
        datasource.setMaxActive(jdbcConfig.getMaxActive());
        datasource.setInitialSize(jdbcConfig.getInitialSize());
        datasource.setMaxWait(jdbcConfig.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(
            jdbcConfig.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(
            jdbcConfig.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(jdbcConfig.getValidationQuery());
        datasource.setTestWhileIdle(jdbcConfig.isTestWhileIdle());
        datasource.setTestOnBorrow(jdbcConfig.isTestOnBorrow());
        datasource.setTestOnReturn(jdbcConfig.isTestOnReturn());
        datasource
            .setPoolPreparedStatements(jdbcConfig.isPoolPreparedStatements());
        datasource.setMaxPoolPreparedStatementPerConnectionSize(
            jdbcConfig.getMaxPoolPreparedStatementPerConnectionSize());
        try {
            datasource.setFilters(jdbcConfig.getFilters());
        } catch (SQLException e) {
            LOGGER.error("druid configuration initialization filter", e);
            return null;
        }
        datasource
            .setConnectionProperties(jdbcConfig.getConnectionProperties());
        return datasource;
    }

    /**
     * 获取 sql session factory
     * 
     * @param dataSource
     *            DataSource
     * @param config
     *            mybatis config location
     * @param mapperLoc
     *            mapper xml文件位置
     * @return SqlSessionFactory
     */
    static SqlSessionFactory createSqlSessionFactory(DataSource dataSource,
        String config, String mapperLoc) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        /* 设置 mybatis configuration 扫描路径 */
        bean.setConfigLocation(new ClassPathResource(config));
        try {
            /* 添加 mapper 扫描路径 */
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            //bean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
            bean.setMapperLocations(resolver.getResources(mapperLoc));
            return bean.getObject();
        } catch (Exception e) {
            LOGGER.error("create sql session factory failed", e);
            throw new RuntimeException(e);
        }
    }
}
