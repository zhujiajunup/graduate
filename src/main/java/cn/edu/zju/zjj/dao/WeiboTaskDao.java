/**
 * @(#)WeiboTaskDao.java, 2018/1/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.dao;

import cn.edu.zju.zjj.entity.WeiboTask;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Repository
public interface WeiboTaskDao extends BaseDao<WeiboTask> {
    List<WeiboTask> getAll();
}