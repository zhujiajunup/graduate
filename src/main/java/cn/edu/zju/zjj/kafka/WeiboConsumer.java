package cn.edu.zju.zjj.kafka;

import cn.edu.zju.zjj.entity.BaseEntity;
import cn.edu.zju.zjj.entity.WeiboTweet;
import cn.edu.zju.zjj.entity.WeiboUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.*;

/**
 * author: zjj
 */
@Slf4j
public class WeiboConsumer {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private KafkaConsumer<String, String> kafkaConsumer;

    public WeiboConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "weibo");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Arrays.asList("sina_weibo"));
    }

    public List<BaseEntity> receive() throws InterruptedException {

        ConsumerRecords<String, String> records = this.kafkaConsumer.poll(1_000);
        List<BaseEntity> entities = new ArrayList<>();
        records.forEach(
                record -> {
                    try {
                        log.info("get message: {}", record.value());
                        Map<String, String> map = MAPPER.readValue(record.value(), Map.class);
                        if ("user_info".equals(map.get("type"))) {
                            WeiboUser user = MAPPER.readValue(record.value(), WeiboUser.class);
                            entities.add(user);
                        }if("tweet_info".equals(map.get("type"))){
                            WeiboTweet tweet = MAPPER.readValue(record.value(), WeiboTweet.class);
                            log.info(tweet.getContent());
                            entities.add(tweet);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        return entities;

    }

    public static void main(String[] args) throws InterruptedException {

    }

}
