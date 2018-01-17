package cn.edu.zju.zjj.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * data source factory
 * 
 * @author 祝佳俊(zhujiajunup@163.com)
 */
class Factory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);


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
