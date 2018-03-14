/**
 * @(#)ProvinceDao.java, 2018/3/14.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.dao;

import org.springframework.stereotype.Repository;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Repository
public interface ProvinceDao {

    String getCapital(String province);
}