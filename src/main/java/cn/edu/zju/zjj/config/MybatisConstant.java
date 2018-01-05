/**
 * @(#)MybatisConfig.java, 2017/7/24.
 * <p/>
 * Copyright 2017 HEHE, Inc. All rights reserved.
 * HEHE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.config;

/**
 * mybatis 相关的常量
 * 
 * @author 祝佳俊(zhujiajunup@163.com)
 */
class MybatisConstant {

    /**
     * sqlSessionFactory字符常量
     */
    static final String SSF_REF = "sqlSessionFactory";

    /**
     * dataSource
     */
    static final String DATA_SOURCE = "dataSource";

    /**
     * mybatis 配置xml文件位置
     */
    static final String MYBATIS_CONFIG = "mybatis/mybatis-config.xml";
/**
     * project mapper的xml文件的位置
     */
    static final String MAPPER_LOCATIONS = "classpath:/cn/edu/zju/zjj/dao/mapper/*.xml";

}
