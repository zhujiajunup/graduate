package cn.edu.zju.zjj.controller;

import cn.edu.zju.zjj.bean.ResponseBean;
import cn.edu.zju.zjj.dao.WeiboCommentDao;
import cn.edu.zju.zjj.entity.BaseEntity;
import cn.edu.zju.zjj.entity.SourceType;
import cn.edu.zju.zjj.entity.WeiboComment;
import cn.edu.zju.zjj.entity.WeiboUser;
import cn.edu.zju.zjj.service.WeiboCommentService;
import cn.edu.zju.zjj.service.WeiboUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import scala.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2017/12/16 15:56
 */
@Slf4j
@RestController
@RequestMapping(value = "/comment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WeiboCommentController {
    private WeiboCommentService commentService;
    private WeiboUserService userService;

    @Autowired
    public WeiboCommentController(WeiboCommentService commentService, WeiboUserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @RequestMapping(value = "/source/stat", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getCommentSource(@RequestBody Map<String, Object> params) {
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String tweetId = (String) params.get("tweetId");
        bean.setData(this.commentService.statSource(tweetId));
        return bean;
    }

    @RequestMapping(value = "/place/stat", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getCommentPlace(@RequestBody Map<String, Object> params) {
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String tweetId = (String) params.get("tweetId");
        bean.setData(this.commentService.statPlace(tweetId));
        // TODO
        //WeiboUser user = this.userService.getPublisher(tweetId);

        return bean;
    }

    @RequestMapping(value = "/timeCnt/stat", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getCommentTimeCount(@RequestBody Map<String, Object> params) {
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String tweetId = (String) params.get("tweetId");
        bean.setData(this.commentService.statTime(tweetId));
        return bean;
    }

    @RequestMapping(value = "/wordCloud", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getWordCloud(@RequestBody Map<String, Object> params) {
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String tweetId = (String) params.get("tweetId");
        bean.setData(this.commentService.wordCloud(tweetId));
        return bean;
    }

    @RequestMapping(value = "/cityCnt", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean cityCnt(@RequestBody Map<String, Object> params) {
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String tweetId = (String) params.get("tweetId");
        bean.setData(this.commentService.getPlaceCnt(tweetId));
        return bean;
    }

    @RequestMapping(value = "/top10", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean commentTop(@RequestBody Map<String, Object> params) {
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String tweetId = (String) params.get("tweetId");
        int type = (Integer) params.get("type");
        List<WeiboComment> comments = this.commentService.top(type, tweetId);

        List<Object> result = new ArrayList<>();
        comments.forEach(
                comment -> {

                    WeiboUser weiboUser = this.userService.getUser(comment.getUserId());
                    if (weiboUser != null) {
                        result.add(new HashMap<String, BaseEntity>() {
                            {
                                put("user", weiboUser);
                                put("comment", comment);
                            }
                        });
                    }
                }
        );
        bean.setData(result);
        return bean;
    }

}
