package cn.edu.zju.zjj;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import cn.edu.zju.zjj.entity.BaseEntity;
import cn.edu.zju.zjj.entity.WeiboComment;
import cn.edu.zju.zjj.entity.WeiboTweet;
import cn.edu.zju.zjj.entity.WeiboUser;
import cn.edu.zju.zjj.kafka.WeiboConsumer;
import cn.edu.zju.zjj.service.JedisService;
import cn.edu.zju.zjj.service.WeiboCommentService;
import cn.edu.zju.zjj.service.WeiboTweetService;
import cn.edu.zju.zjj.service.WeiboUserService;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 */
@Slf4j
@SpringBootApplication
@EnableScheduling
public class App {
    private WeiboUserService weiboUserService;

    private WeiboTweetService weiboTweetService;

    private WeiboCommentService weiboCommentService;

    private JedisService jedisService;

    //private WeiboConsumer weiboConsumer = new WeiboConsumer();

    public static final AtomicLong update = new AtomicLong(0);

    public static final AtomicLong insert = new AtomicLong(0);

    private long updateBefore = update.get();

    private long insertBefore = insert.get();

    public static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${jedis.user}")
    private boolean ifPushUser;

    @Autowired
    public App(WeiboUserService weiboUserService,
        WeiboTweetService weiboTweetService,
        WeiboCommentService weiboCommentService, JedisService jedisService) {
        this.weiboUserService = weiboUserService;
        this.weiboTweetService = weiboTweetService;
        this.weiboCommentService = weiboCommentService;
        this.jedisService = jedisService;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void qps() {
        long updateNow = update.get();
        long insertNow = insert.get();

        log.info("-----------------({}-{})-----{}/s---------------", updateNow,
            updateBefore, (updateNow - updateBefore) / 60);
        log.info("-----------------({}-{})-----{}/s---------------", insertNow,
            insertBefore, (insertNow - insertBefore) / 60);
        insertBefore = insertNow;
        updateBefore = updateNow;
    }

    private void process(BaseEntity baseEntity) {
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
            log.error("", e);
        }
    }

    private void pushUser() {
        List<WeiboUser> users = this.weiboUserService.getByLimit(50_000);
        users.forEach(user -> {
            log.info("push to redis: {}", user);
                    this.jedisService.pushToList("user",
                            new HashMap<String, Object>() {
                                {
                                    put("user_id", String.valueOf(user.getId()));
                                }
                            });
                }

        );
    }

    /*private void run() {
        String dbType = this.jdbcUrl.split(":")[1];
        log.info("db type: {}", dbType);
        if(ifPushUser) {
            log.info("push user info to redis");
            threadPool.submit(this::pushUser);
        }
        while (true) {
            try {
                List<BaseEntity> entities = weiboConsumer.receive();
                entities.forEach(baseEntity -> {
                    if (dbType.equals("sqlite")) {
                        process(baseEntity);
                    } else {
                        threadPool.submit(() -> process(baseEntity));
                    }
                    //log.info("{}", baseEntity);

                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }*/

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);
        //context.getBean(App.class).run();
    }
}
