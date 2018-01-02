package cn.edu.zju.zjj;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import cn.edu.zju.zjj.entity.BaseEntity;
import cn.edu.zju.zjj.entity.WeiboComment;
import cn.edu.zju.zjj.entity.WeiboTweet;
import cn.edu.zju.zjj.entity.WeiboUser;
import cn.edu.zju.zjj.kafka.WeiboConsumer;
import cn.edu.zju.zjj.service.WeiboCommentService;
import cn.edu.zju.zjj.service.WeiboTweetService;
import cn.edu.zju.zjj.service.WeiboUserService;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 */
@Slf4j
@SpringBootApplication
public class App {
    private WeiboUserService weiboUserService;
    private WeiboTweetService weiboTweetService;
    private WeiboCommentService weiboCommentService;
    private WeiboConsumer weiboConsumer = new WeiboConsumer();

    @Autowired
    public App(WeiboUserService weiboUserService, WeiboTweetService weiboTweetService, WeiboCommentService weiboCommentService) {
        this.weiboUserService = weiboUserService;
        this.weiboTweetService = weiboTweetService;
        this.weiboCommentService = weiboCommentService;
    }

    private void run() {
        while (true) {
            try {
                List<BaseEntity> entities = weiboConsumer.receive();
                entities.forEach(
                        baseEntity -> {

                            log.info("{}", baseEntity);
                            try {
                                if (baseEntity instanceof WeiboUser) {

                                    weiboUserService.insertOrUpdate((WeiboUser) baseEntity);
                                } else if (baseEntity instanceof WeiboTweet) {

                                    weiboTweetService.insertOrUpdate((WeiboTweet) baseEntity);

                                } else if (baseEntity instanceof WeiboComment) {
                                    weiboCommentService.insertOrUpdate((WeiboComment) baseEntity);
                                }
                            } catch (Exception e) {
                                log.error("{}", baseEntity);
                            }
                        }
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);
        context.getBean(App.class).run();
    }
}
