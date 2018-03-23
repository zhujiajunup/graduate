package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.dao.WeiboCommentDao;
import cn.edu.zju.zjj.dao.WeiboTweetDao;
import cn.edu.zju.zjj.entity.Leader;
import cn.edu.zju.zjj.entity.Count;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2018/3/18 18:05
 */
@Service
@Slf4j
public class WeiboStatService {

    private WeiboCommentDao commentDao;
    private WeiboTweetDao tweetDao;

    private WeiboStatService(WeiboCommentDao commentDao, WeiboTweetDao tweetDao) {
        this.commentDao = commentDao;
        this.tweetDao = tweetDao;
    }

    public List<Leader> leaders(String tid){

        return this.tweetDao.getLeaders(tid);
    }

    public Map<String, Integer> statSex(String tid){
        Map<String, Integer> result = new HashMap<>();
        List<Count> commentSexCnt = this.commentDao.statSex(tid);
        List<Count> tweetSexCnt = this.tweetDao.statSex(tid);
        commentSexCnt.addAll(tweetSexCnt);
        commentSexCnt.forEach(
                count -> {
                    Integer cnt = result.getOrDefault(count.getKey(), 0);
                    cnt += count.getCount();
                    result.put(count.getKey(), cnt);
                }
        );

        return result;
    }

    public Map<String, Object> statTime(String tid) {
        List<Count> cmtTimeCnts = this.commentDao.statTime(tid);
        List<Count> tweetTimeCnts = this.tweetDao.statTime(tid);
        List<Count> cmt = new ArrayList<>();
        List<Count> tweet = new ArrayList<>();
        int i = 0;
        int j = 0;
        for (; i < cmtTimeCnts.size(); ) {
            if (j >= tweetTimeCnts.size()) {
                break;
            }
            int r = cmtTimeCnts.get(i).getKey().compareTo(tweetTimeCnts.get(j).getKey());
            if (r == 0) {
                cmt.add(cmtTimeCnts.get(i));
                tweet.add(tweetTimeCnts.get(j));
                ++i;
                ++j;
            } else if (r < 0) {
                cmt.add(cmtTimeCnts.get(i));
                tweet.add(new Count(cmtTimeCnts.get(i).getKey(), 0));
                i++;
            } else {
                tweet.add(tweetTimeCnts.get(j));
                cmt.add(new Count(tweetTimeCnts.get(j).getKey(), 0));
                j++;
            }
        }
        if( i < cmtTimeCnts.size()){
            cmt.add(cmtTimeCnts.get(i));
            tweet.add(new Count(cmtTimeCnts.get(i).getKey(), 0));
            ++i;
        }
        if( j < tweetTimeCnts.size()){
            tweet.add(tweetTimeCnts.get(j));
            cmt.add(new Count(tweetTimeCnts.get(j).getKey(), 0));
            ++j;
        }
        Map<String, Object> timeCntMap = new HashMap<>();
        timeCntMap.put("转发数", tweet);
        timeCntMap.put("评论数", cmt);
        return timeCntMap;
    }
}
