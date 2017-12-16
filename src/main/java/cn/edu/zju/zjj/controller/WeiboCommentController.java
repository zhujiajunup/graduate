package cn.edu.zju.zjj.controller;

import cn.edu.zju.zjj.bean.ResponseBean;
import cn.edu.zju.zjj.dao.WeiboCommentDao;
import cn.edu.zju.zjj.entity.SourceType;
import cn.edu.zju.zjj.service.WeiboCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    WeiboCommentService commentService;

    @Autowired
    public WeiboCommentController(WeiboCommentService commentService){
        this.commentService = commentService;
    }

    @RequestMapping(value = "/source/stat", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getCommentSource(@RequestBody Map<String, Object> params){
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String tweetId = (String)params.get("tweetId");
        bean.setData(this.commentService.statSource(tweetId));

        return bean;
    }
}
