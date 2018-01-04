/**
 * @(#)SqliteTest.java, 2018/1/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj;

import cn.edu.zju.zjj.dao.WeiboCommentDao;
import cn.edu.zju.zjj.entity.WeiboComment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 祝佳俊(zhujijunup@163.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SqliteTest {
    @Autowired
    private WeiboCommentDao commentDao;

    @Test
    public void test(){
        WeiboComment comment = new WeiboComment();
        comment.setId("123123");
        comment.setContent("hellow");
        comment.setPubTime("2018-01-04 11:23:00");
        comment.setSource("iphone");
        comment.setTweetId("addc234");
        comment.setUserId("12312312");
        commentDao.insert(comment);
    }
}