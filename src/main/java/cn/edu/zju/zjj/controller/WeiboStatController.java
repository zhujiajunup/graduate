package cn.edu.zju.zjj.controller;

import cn.edu.zju.zjj.bean.ResponseBean;
import cn.edu.zju.zjj.entity.WeiboComment;
import cn.edu.zju.zjj.service.WeiboCommentService;
import cn.edu.zju.zjj.service.WeiboStatService;
import cn.edu.zju.zjj.service.WeiboTweetService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    public WeiboStatController(WeiboStatService statService){
        this.statService = statService;
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
        return new ResponseBean(this.statService.leaders(tid));
    }

    @ResponseBody
    @RequestMapping(value = "/sex")
    public ResponseBean sex(@RequestBody Map<String, Object> parms){
        String tid = (String)parms.get("tweetId");
        return new ResponseBean(this.statService.statSex(tid));
    }


}
