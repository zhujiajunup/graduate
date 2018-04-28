package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.bean.UserTaskBean;
import cn.edu.zju.zjj.dao.UserTaskDao;
import cn.edu.zju.zjj.entity.UserTask;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2018/4/28 10:50
 */
@Service
public class UserTaskService {
    private UserTaskDao userTaskDao;

    public UserTaskService(UserTaskDao userTaskDao){
        this.userTaskDao = userTaskDao;
    }

    public void insert(UserTask userTask){
        this.userTaskDao.insert(userTask);
    }

    public List<UserTaskBean> getAll(){
        return this.userTaskDao.getAll();
    }
}
