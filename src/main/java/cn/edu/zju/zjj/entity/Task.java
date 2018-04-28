package cn.edu.zju.zjj.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2018/4/28 9:32
 */
@Getter
@Setter
@ToString
public class Task {
    private long id;
    private long createTime;
    private String status;
}
