package cn.edu.zju.zjj;

import cn.edu.zju.zjj.dao.WeiboUserDao;
import cn.edu.zju.zjj.entity.BaseEntity;
import cn.edu.zju.zjj.entity.WeiboTweet;
import cn.edu.zju.zjj.entity.WeiboUser;
import cn.edu.zju.zjj.kafka.WeiboConsumer;
import cn.edu.zju.zjj.service.WeiboTweetService;
import cn.edu.zju.zjj.service.WeiboUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Hello world!
 */
@Slf4j
@SpringBootApplication
public class App {
    private WeiboUserService weiboUserService;
    private WeiboTweetService weiboTweetService;
    private WeiboConsumer weiboConsumer = new WeiboConsumer();

    @Autowired
    public App(WeiboUserService weiboUserService, WeiboTweetService weiboTweetService) {
        this.weiboUserService = weiboUserService;
        this.weiboTweetService = weiboTweetService;
    }

    private void run() {
        while (true) {
            try {
                List<BaseEntity> entities = weiboConsumer.receive();
                entities.forEach(
                        baseEntity -> {
                            if (baseEntity instanceof WeiboUser) {
                                weiboUserService.insertOrUpdate((WeiboUser) baseEntity);
                            } else if (baseEntity instanceof WeiboTweet) {
                                try {
                                    weiboTweetService.insertOrUpdate((WeiboTweet) baseEntity);
                                }catch (Exception e){
                                    log.error("{}", baseEntity);
                                }
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
