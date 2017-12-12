package cn.edu.zju.zjj.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 14:54
 */
@Setter
@Getter
@ToString(of = "id")
public class BaseEntity {
    private String id;
    private String type;
}
