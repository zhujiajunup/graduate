package cn.edu.zju.zjj.dao;

import cn.edu.zju.zjj.entity.Chain;
import cn.edu.zju.zjj.entity.Leader;
import cn.edu.zju.zjj.entity.Count;
import cn.edu.zju.zjj.entity.WeiboTweet;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * author: zhujiajunup@163.com
 * <p>
 * Data: 2017/12/9 18:54
 */
@Repository
public interface WeiboTweetDao extends BaseDao<WeiboTweet> {
    int getTotal(String uid);
    List<WeiboTweet> getByUid(String uid);
    List<WeiboTweet> getByPage(String uid, int pageSize, int offset);
    List<WeiboTweet> getRepost(String tid);
    List<Chain> getChain(String tid);
    List<Count> statTime(String tweetId);
    List<Leader> getLeaders(String tweetId);
    List<Count> statSex(String tid);
    List<String> getPlace(String tid);
}
