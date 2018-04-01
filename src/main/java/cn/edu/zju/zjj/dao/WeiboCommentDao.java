package cn.edu.zju.zjj.dao;

import cn.edu.zju.zjj.entity.SourceType;
import cn.edu.zju.zjj.entity.Count;
import cn.edu.zju.zjj.entity.WeiboComment;
import cn.edu.zju.zjj.entity.WeiboUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/15 22:42
 */
@Repository
public interface WeiboCommentDao extends BaseDao<WeiboComment>{

    List<SourceType> statSource(String tweetId);

    List<Count> statTime(String tweetId);
    List<Count> statSex(String tweetId);

    List<WeiboComment> getByTweetId(String tweetId);

    List<WeiboUser> getUsers(String tweetId);

    List<String> getPlace(String tweetId);

    List<WeiboComment> top(@Param("tid")String tid, @Param("type") int type);
}
