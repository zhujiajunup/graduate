package cn.edu.zju.zjj.bean;

import cn.edu.zju.zjj.entity.WeiboUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2018/4/28 10:58
 */
@Setter
@Getter
@ToString
public class UserTaskBean extends WeiboUser{
    private long createTime;
    private String status;
    private String userId;

}
