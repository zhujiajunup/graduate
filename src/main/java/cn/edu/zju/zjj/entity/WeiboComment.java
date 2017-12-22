package cn.edu.zju.zjj.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/15 22:39
 */
@Setter
@Getter
@ToString(of = {"userId", "pubTime", "source"})
public class WeiboComment extends BaseEntity{
    private String userId;
    private String content;
    private String pubTime;
    private String source;
    private String tweetId;
}
