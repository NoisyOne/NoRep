package org.example.server.constant;

/**
 * 通信协议常量定义
 */
public class ProtocolConstant {
    // 特殊用户ID
    public static final String SERVER_USER_ID = "SERVER";
    public static final String SYSTEM_USER_ID = "SYSTEM";

    // 消息类型
    public static final int WELCOME_MESSAGE = 0;          // 欢迎消息
    public static final int LOGIN_REQUEST = 1;            // 登录请求
    public static final int LOGIN_SUCCESS = 2;            // 登录成功
    public static final int LOGIN_FAILURE = 3;            // 登录失败
    public static final int LOGOUT_REQUEST = 4;           // 登出请求
    public static final int PRIVATE_MESSAGE = 5;          // 私聊消息
    public static final int GROUP_MESSAGE = 6;            // 群组消息
    public static final int FRIEND_REQUEST = 7;           // 好友请求
    public static final int ONLINE_STATUS_REQUEST = 8;    // 在线状态请求
    public static final int USER_ONLINE_STATUS = 9;       // 用户在线状态
    public static final int DELIVERY_RECEIPT = 10;        // 送达回执
    public static final int READ_RECEIPT = 11;            // 已读回执
    public static final int OFFLINE_MESSAGE_NOTIFICATION = 12; // 离线消息通知
    public static final int UNKNOWN_MESSAGE_TYPE = 13;    // 未知消息类型
    public static final int REGISTER_REQUEST = 14;        // 注册请求
    public static final int REGISTER_SUCCESS = 15;        // 注册成功
    public static final int REGISTER_FAILURE = 16;        // 注册失败
    public static final int ONLINE_USERS_LIST = 17;       // 在线用户列表
    public static final int UNEXPECTED_MESSAGE = 18;      // 意外消息

    // 消息内容类型
    public static final int CONTENT_TEXT = 0;             // 文本内容
    public static final int CONTENT_IMAGE = 1;            // 图像内容
    public static final int CONTENT_FILE = 2;             // 文件内容

    // 用户状态
    public static final int USER_STATUS_ACTIVE = 1;       // 活跃状态
    public static final int USER_STATUS_DISABLED = 2;     // 禁用状态
    public static final int USER_STATUS_PENDING = 3;      // 待激活状态

    // 群组角色
    public static final int GROUP_ROLE_MEMBER = 0;        // 普通成员
    public static final int GROUP_ROLE_ADMIN = 1;         // 管理员
    public static final int GROUP_ROLE_OWNER = 2;         // 群主

    // 系统消息
    public static final String WELCOME_MESSAGE_CONTENT = "Welcome to Chat Server";
    public static final int AUTH_FAILURE = 19;
}
