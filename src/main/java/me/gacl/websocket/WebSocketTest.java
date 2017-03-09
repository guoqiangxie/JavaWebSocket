package me.gacl.websocket;

import com.alibaba.fastjson.JSON;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/websocket")
public class WebSocketTest {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
//    private static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet<WebSocketTest>();

    private static Map<String, WebSocketTest> webSocketUserMap = new Hashtable<String, WebSocketTest>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String userName;

    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
//        webSocketSet.add(this);     //加入set中

        String userName = session.getRequestParameterMap().get("userName").get(0);
        this.userName = userName;
        webSocketUserMap.put(userName, this);

        addOnlineCount();           //在线数加1
        WebSocketMessage message = new WebSocketMessage();
        message.setType(3);
        sendMessageToClient(message);
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
//        webSocketSet.remove(this);  //从set中删除

        webSocketUserMap.remove(this.userName);
        subOnlineCount();           //在线数减1
        WebSocketMessage message = new WebSocketMessage();
        message.setType(3);
        sendMessageToClient(message);
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        //群发消息
//        for (WebSocketTest item : webSocketSet) {
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//                continue;
//            }
//        }

        WebSocketMessage webSocketMessage = JSON.parseObject(message, WebSocketMessage.class);
        sendMessageToClient(webSocketMessage);
    }

    private void sendMessageToClient(WebSocketMessage socketMessage) {
        if (socketMessage.getType() == 1) {
            for (WebSocketTest item : webSocketUserMap.values()) {
                try {
                    item.sendMessage(this.userName + "群发消息：" + socketMessage.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } else if (socketMessage.getType() == 2) {
            WebSocketTest webSocketTest = webSocketUserMap.get(socketMessage.getUserName());
            try {
                webSocketTest.sendMessage(this.userName + "给您发消息：" + socketMessage.getMessage());
                this.sendMessage("您给" + webSocketTest.userName + "发送的消息:" + socketMessage.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            for (WebSocketTest item : webSocketUserMap.values()) {
                try {
                    item.sendMessage(this.userName + "登录，目前在线好友:" + webSocketUserMap.keySet().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketTest.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketTest.onlineCount--;
    }
}
