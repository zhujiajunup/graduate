package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.constant.AppConstant;
import cn.edu.zju.zjj.dao.ProvinceDao;
import cn.edu.zju.zjj.dao.WeiboCommentDao;
import cn.edu.zju.zjj.dao.WeiboTweetDao;
import cn.edu.zju.zjj.entity.Leader;
import cn.edu.zju.zjj.entity.Count;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private ProvinceDao provinceDao;
    private WeiboStatService(ProvinceDao provinceDao, WeiboCommentDao commentDao, WeiboTweetDao tweetDao) {
        this.commentDao = commentDao;
        this.tweetDao = tweetDao;
        this.provinceDao = provinceDao;
    }

    public List<Leader> leaders(String tid){

        return this.tweetDao.getLeaders(tid);
    }
    public Map<String, Integer> statPlace(String tid){
        List<String> places = this.commentDao.getPlace(tid);
        places.addAll(this.tweetDao.getPlace(tid));
        Map<String, Integer> placeCnt = new TreeMap<>();
        Map<String, Integer> reslut = new HashMap<>();
        places.forEach(place -> {
            String[] fields = place.split(" ");
            String province = null;
            if(fields.length == 2){
                if(!"其他".equals(fields[1])){
                    if(AppConstant.SPECIAL_PROVINCE.contains(fields[0])){
                        province = fields[0];
                    }else {
                        int cnt = placeCnt.getOrDefault(fields[1], 0);
                        placeCnt.put(fields[1], ++cnt);
                    }
                }else{
                    province = fields[0];
                }
            }else if (fields.length == 1) {
                province = fields[0];
            }else{
                log.warn(place);
            }
            if(province != null){
                Optional<String> capitalOpt = Optional
                        .ofNullable(this.provinceDao.getCapital(province));
                if (capitalOpt.isPresent()) {
                    int cnt = placeCnt.getOrDefault(capitalOpt.get(), 0);
                    placeCnt.put(capitalOpt.get(), ++cnt);
                } else {
                    log.warn(place);
                }
            }
        });
        List<Map.Entry<String, Integer>> entryArrayList = new ArrayList<>(placeCnt.entrySet());
        entryArrayList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        int count = 0;

        Iterator<Map.Entry<String, Integer>> keyIter = entryArrayList.iterator();
        while (keyIter.hasNext() && count <= 20){
            Map.Entry<String, Integer> entry = keyIter.next();
            reslut.put(entry.getKey(), entry.getValue());
            count ++;
        }
        return reslut;
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
