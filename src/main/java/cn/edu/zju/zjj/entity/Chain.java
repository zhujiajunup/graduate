package cn.edu.zju.zjj.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 *  微博传播连
 *
 * Data: 2018/3/17 9:18
 */
@ToString
@Getter
@Setter
public class Chain {

    /**
     * 微博发布者
     */
    private String from;
    /**
     * 微博发布者微博id
     */
    private String fromTid;
    /**
     * 转发者
     */
    private String to;
    /**
     * 转发微博Id
     */
    private String toTid;
}
