package cn.edu.zju.zjj.constant;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2017/12/16 16:02
 */
public enum ResponseCode {

    SUCCESS(200, "success"),
    ILLEGAL_ARGUMENTS(400, "你的请求中有参数错误"),
    NOT_LOGIN(401,  "你没有登录"),
    NO_PERMISSION(403, "你没有权限访问这项资源"),
    NOT_FOUND(404, "没有找到该资源"),
    ILLEGAL_ACTION(460, "你的操作不合法"),
    SERVER_ERROR(500, "服务器错误"),
    NOT_IMPLEMENTED(501, "这项功能没有被实现");

    public final int code;

    public final String defaultMsg;

    ResponseCode(int code, String defaultMsg) {
        this.code = code;
        this.defaultMsg = defaultMsg;
    }

    public String getMsg(Exception e) {
        if (e.getMessage() != null) {
            return e.getMessage();
        } else {
            return defaultMsg;
        }
    }

}
