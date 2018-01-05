package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.dao.WeiboUserDao;
import cn.edu.zju.zjj.entity.WeiboUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 15:39
 */
@Service
public class WeiboUserService extends BaseService<WeiboUser>{

    private WeiboUserDao userDao;
    @Autowired
    private WeiboUserService(WeiboUserDao weiboUserDao){
        super(weiboUserDao);
        this.userDao = weiboUserDao;
    }

    public WeiboUser getPublisher(String tweetId){
        return this.userDao.getPublisher(tweetId);
    }

    public WeiboUser getUser(String uid){
        return this.userDao.getById(uid);
    }
}
