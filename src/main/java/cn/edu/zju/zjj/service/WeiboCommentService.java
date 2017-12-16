package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.dao.BaseDao;
import cn.edu.zju.zjj.dao.WeiboCommentDao;
import cn.edu.zju.zjj.entity.SourceType;
import cn.edu.zju.zjj.entity.WeiboComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2017/12/15 22:47
 */
@Service
public class WeiboCommentService extends  BaseService<WeiboComment> {
    WeiboCommentDao commentDao;
    @Autowired
    public WeiboCommentService(WeiboCommentDao commentDao) {
        super(commentDao);
        this.commentDao = commentDao;
    }

    public List<SourceType> statSource(String tweetId){
        return this.commentDao.statSource(tweetId);
    }
}
