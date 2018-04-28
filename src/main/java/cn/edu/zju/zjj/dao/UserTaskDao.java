package cn.edu.zju.zjj.dao;

import cn.edu.zju.zjj.bean.UserTaskBean;
import cn.edu.zju.zjj.entity.UserTask;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2018/4/28 10:50
 */
@Repository
public interface UserTaskDao extends BaseDao<UserTask> {
    List<UserTaskBean> getAll();
}
