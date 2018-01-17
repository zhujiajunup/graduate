package cn.edu.zju.zjj.dao;

import cn.edu.zju.zjj.entity.WeiboUser;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 14:51
 */
@Repository
public interface WeiboUserDao extends BaseDao<WeiboUser>{
    WeiboUser getPublisher(String tweetId);
    List<WeiboUser> getByLimit(int limit);
}
