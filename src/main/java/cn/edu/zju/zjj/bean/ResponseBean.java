package cn.edu.zju.zjj.bean;

import cn.edu.zju.zjj.constant.ResponseCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2017/12/16 16:04
 */
@Setter
@Getter
@ToString
public class ResponseBean {
    /**
     * 请求状态码，默认为200
     */
    private int code = ResponseCode.SUCCESS.code;

    /**
     * 请求返回的message
     */
    private String msg = "SUCCESS";

    /**
     * 返回的data
     */
    private Object data = null;

    public ResponseBean() {
        this(null);
    }

    public ResponseBean(Object data) {
        this(ResponseCode.SUCCESS, data);
    }

    public ResponseBean(ResponseCode code, Object data) {
        this(code, code.defaultMsg, data);
    }

    public ResponseBean(ResponseCode code, String msg, Object data) {
        this.code = code.code;
        this.msg = msg;
        this.data = data;
    }

}
