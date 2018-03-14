package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.bean.PlaceCount;
import cn.edu.zju.zjj.bean.WordCount;
import cn.edu.zju.zjj.constant.AppConstant;
import cn.edu.zju.zjj.dao.BaseDao;
import cn.edu.zju.zjj.dao.ProvinceDao;
import cn.edu.zju.zjj.dao.WeiboCommentDao;
import cn.edu.zju.zjj.entity.SourceType;
import cn.edu.zju.zjj.entity.TimeCount;
import cn.edu.zju.zjj.entity.WeiboComment;
import cn.edu.zju.zjj.entity.WeiboUser;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import scala.Int;

import java.util.*;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2017/12/15 22:47
 */
@Service
@Slf4j
public class WeiboCommentService extends BaseService<WeiboComment> {

    private WeiboCommentDao commentDao;

    private ProvinceDao provinceDao;

    @Autowired
    public WeiboCommentService(WeiboCommentDao commentDao,
        ProvinceDao provinceDao) {
        super(commentDao);
        this.provinceDao = provinceDao;
        this.commentDao = commentDao;
    }

    public Map<String, Integer> statPlace(String tweetId) {
        List<String> places = this.commentDao.getPlace(tweetId);
        Map<String, Integer> placeCnt = new HashMap<>();
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
        return placeCnt;
    }

    public List<SourceType> statSource(String tweetId) {
        return this.commentDao.statSource(tweetId);
    }

    public List<TimeCount> statTime(String tweetId) {
        return this.commentDao.statTime(tweetId);
    }

    public List<WordCount> wordCloud(String tweetId) {
        Map<String, Integer> wordCount = new TreeMap<>();
        Set<String> tagSet = new HashSet<String>() {
            {
                add("a");
                add("ad");
                add("i");
                add("n");
                add("nr");
                add("ns");
                add("nt");
                add("v");
                add("t");
                add("vn");
            }
        };
        List<WeiboComment> comments = this.commentDao.getByTweetId(tweetId);
        comments.forEach(comment -> {
            Result result = ToAnalysis.parse(comment.getContent());
            result.forEach(term -> {

                if (tagSet.contains(term.getNatureStr())
                    && term.getName().length() > 1) {
                    Integer count = wordCount.getOrDefault(term.getName(), 0);
                    count++;
                    wordCount.put(term.getName(), count);
                }
            });
        });
        List<WordCount> wordCounts = new ArrayList<>();
        wordCount.forEach((w, c) -> {
            WordCount wc = new WordCount();
            wc.setCount(c);
            wc.setWord(w);
            wordCounts.add(wc);
        });
        return wordCounts;
    }

    public Map<String, Integer> getPlaceCnt(String tweetId) {

        Map<String, Integer> countMap = new HashMap<>();
        List<WeiboUser> users = commentDao.getUsers(tweetId);
        users.forEach(weiboUser -> {
            if (weiboUser != null) {
                String place = weiboUser.getPlace();
                String[] fields = place.split(" ");
                if (fields.length == 2) {
                    Integer count = countMap.getOrDefault(fields[1], 0);
                    count++;
                    countMap.put(fields[1], count);
                }
            }
        });
        return countMap;
    }
}
