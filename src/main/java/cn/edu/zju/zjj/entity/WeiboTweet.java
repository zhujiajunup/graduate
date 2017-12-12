package cn.edu.zju.zjj.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 18:34
 */
@Setter
@Getter
@ToString(of = {"content", "time", "source"}, callSuper = true)
public class WeiboTweet extends BaseEntity {
    private String time;
    private String source;
    private String like;
    private String transfer;
    private String comment;
    private String content;
    private long uid;
    private String flag;
    private String sourceTid;
}
