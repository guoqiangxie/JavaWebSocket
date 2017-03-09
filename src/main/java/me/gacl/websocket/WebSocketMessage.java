package me.gacl.websocket;

import java.io.Serializable;

/**
 * Created by xieguoqiang on 2017/3/6.
 */
public class WebSocketMessage implements Serializable {
    private int type; //类型 1. 群发消息 2. 单发消息  3. 登录消息
    private String message;
    private String userName; // 如果type=2，这个字段指定接收人

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
