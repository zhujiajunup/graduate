package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.dao.TopicTaskDao;
import cn.edu.zju.zjj.entity.TopicTask;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2018/4/28 18:54
 */
@Service
public class TopicTaskService {

    private TopicTaskDao topicTaskDao;

    public TopicTaskService(TopicTaskDao topicTaskDao){
        this.topicTaskDao = topicTaskDao;
    }

    public void insert(TopicTask topicTask){
        this.topicTaskDao.insert(topicTask);
    }

    public List<TopicTask> getAll(){
        return this.topicTaskDao.getAll();
    }
}
