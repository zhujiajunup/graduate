package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.dao.WeiboTweetDao;
import cn.edu.zju.zjj.entity.WeiboTweet;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 18:57
 */
@Service
public class WeiboTweetService extends  BaseService<WeiboTweet>{

    private WeiboTweetDao tweetDao;

    @Autowired
    private WeiboTweetService(WeiboTweetDao tweetDao){
        super(tweetDao);
        this.tweetDao = tweetDao;
    }

    public List<WeiboTweet> getByUid(String uid){
        return this.tweetDao.getByUid(uid);
    }
}
