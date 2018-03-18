package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.controller.WeiboCommentController;
import cn.edu.zju.zjj.dao.WeiboCommentDao;
import cn.edu.zju.zjj.dao.WeiboTweetDao;
import cn.edu.zju.zjj.entity.TimeCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Time;
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

    public Map<String, Object> statTime(String tid) {
        List<TimeCount> cmtTimeCnts = this.commentDao.statTime(tid);
        List<TimeCount> tweetTimeCnts = this.tweetDao.statTime(tid);
        List<TimeCount> cmt = new ArrayList<>();
        List<TimeCount> tweet = new ArrayList<>();
        int i = 0;
        int j = 0;
        for (; i < cmtTimeCnts.size(); ) {
            if (j >= tweetTimeCnts.size()) {
                break;
            }
            int r = cmtTimeCnts.get(i).getT().compareTo(tweetTimeCnts.get(j).getT());
            if (r == 0) {
                cmt.add(cmtTimeCnts.get(i));
                tweet.add(tweetTimeCnts.get(j));
                ++i;
                ++j;
            } else if (r < 0) {
                cmt.add(cmtTimeCnts.get(i));
                tweet.add(new TimeCount(cmtTimeCnts.get(i).getT(), 0));
                i++;
            } else {
                tweet.add(tweetTimeCnts.get(j));
                cmt.add(new TimeCount(tweetTimeCnts.get(j).getT(), 0));
                j++;
            }
        }
        if( i < cmtTimeCnts.size()){
            cmt.add(cmtTimeCnts.get(i));
            tweet.add(new TimeCount(cmtTimeCnts.get(i).getT(), 0));
            ++i;
        }
        if( j < tweetTimeCnts.size()){
            tweet.add(tweetTimeCnts.get(j));
            cmt.add(new TimeCount(tweetTimeCnts.get(j).getT(), 0));
            ++j;
        }
        Map<String, Object> timeCntMap = new HashMap<>();
        timeCntMap.put("转发数", tweet);
        timeCntMap.put("评论数", cmt);
        return timeCntMap;
    }
}
