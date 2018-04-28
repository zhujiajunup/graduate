/**
 * @(#)WeiboTask.java, 2018/1/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Setter
@Getter
@ToString
public class WeiboTask extends Task{

    private String weiboId;
    private String weiboUser;
    private String weiboContent;
}