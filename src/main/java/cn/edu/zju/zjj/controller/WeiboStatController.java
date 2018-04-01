package cn.edu.zju.zjj.controller;

import cn.edu.zju.zjj.bean.ResponseBean;
import cn.edu.zju.zjj.entity.Leader;
import cn.edu.zju.zjj.entity.WeiboComment;
import cn.edu.zju.zjj.entity.WeiboTweet;
import cn.edu.zju.zjj.service.WeiboCommentService;
import cn.edu.zju.zjj.service.WeiboStatService;
import cn.edu.zju.zjj.service.WeiboTweetService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2018/3/18 18:05
 */
@RestController
@RequestMapping(value = "/stat", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WeiboStatController {

    private WeiboStatService statService;
    private WeiboTweetService tweetService;
    public WeiboStatController(WeiboTweetService tweetService, WeiboStatService statService){
        this.statService = statService;
        this.tweetService = tweetService;
    }

    @ResponseBody
    @RequestMapping(value = "/time")
    public ResponseBean statTime(@RequestBody Map<String, Object> parms){
        String tid = (String)parms.get("tweetId");
        return new ResponseBean(this.statService.statTime(tid));
    }

    @ResponseBody
    @RequestMapping(value = "/leader")
    public ResponseBean leader(@RequestBody Map<String, Object> parms){
        String tid = (String)parms.get("tweetId");
        List<Leader> leaders = this.statService.leaders(tid);
        List<Object> result = new ArrayList<>();
        leaders.forEach(
                leader -> {
                    WeiboTweet tweet = this.tweetService.getById(leader.getTid());
                    if(tweet != null){
                        result.add(new HashMap<String, Object>(){
                            {
                                put("tweet", tweet);
                                put("leader", leader);
                            }
                        });
                    }
                }
        );
        return new ResponseBean(result);
    }

    @ResponseBody
    @RequestMapping(value = "/sex")
    public ResponseBean sex(@RequestBody Map<String, Object> params){
        String tid = (String)params.get("tweetId");
        return new ResponseBean(this.statService.statSex(tid));
    }
    @ResponseBody
    @RequestMapping(value = "/place")
    public ResponseBean place(@RequestBody Map<String, Object> params){
        String tid = (String)params.get("tweetId");
        return new ResponseBean(this.statService.statPlace(tid));
    }


}
