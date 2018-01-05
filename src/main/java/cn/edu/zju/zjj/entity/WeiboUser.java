package cn.edu.zju.zjj.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 14:51
 */
@Setter
@Getter
@ToString(of = {"nickname"}, callSuper = true)
public class WeiboUser extends BaseEntity{
    private String nickname;
    private String tags;
    private String gender;
    private String place;
    private String signature;
    private String birthday;
    private String sexOrientation;
    private String eduInfo;
    private String marriage;
    private String workInfo;
    private int fansNum;
    private int tweetNum;
    private int followNum;
    private String head;

}
