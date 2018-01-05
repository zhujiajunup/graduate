package cn.edu.zju.zjj.dao;

import cn.edu.zju.zjj.entity.WeiboTweet;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 18:54
 */
@Repository
public interface WeiboTweetDao extends BaseDao<WeiboTweet> {
    List<WeiboTweet> getByUid(String uid);
}
