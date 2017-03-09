<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Java后端WebSocket的Tomcat实现</title>
</head>
<body>
<br/><br/>
    <span id="loginSpan">
        登录用户名<input id="userName" type="text"/>
        <button onclick="login()">登录</button>
    </span><br/><br/><br/>

    <%--<span>--%>
        <%--目前在线人员：<span id="loginUserSpan" />--%>
    <%--</span><br/><br/><br/>--%>


    给<input id="sendUser" type="text"/>发送<input id="text" type="text"/>
    <button onclick="send()">发送消息</button>
    <hr/>
    <button onclick="closeWebSocket()">关闭WebSocket连接</button>
    <hr/>
    <div id="message"></div>
</body>

<script type="text/javascript">
    var websocket = null;


    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    }

    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }

    //关闭WebSocket连接
    function closeWebSocket() {
        websocket.close();
    }

    //发送消息
    function send() {
        var sendUser = document.getElementById('sendUser').value;
        var message = document.getElementById('text').value;
        var sendMsg = "";
        if (sendUser == null || sendUser == '') {
            sendMsg = '{"type":1,"message":"'+message+'"}';
        } else {
            sendMsg = '{"type":2,"message":"'+message+'","userName":"'+sendUser+'"}';
        }
        websocket.send(sendMsg);
    }

    function login() {
        var userName = document.getElementById('userName').value;
        //判断当前浏览器是否支持WebSocket
        if ('WebSocket' in window) {
            websocket = new WebSocket("ws://172.168.11.21:8080/websocket?userName=" + userName);
            //连接发生错误的回调方法
            websocket.onerror = function () {
                setMessageInnerHTML("WebSocket连接发生错误");
            };

            //连接成功建立的回调方法
            websocket.onopen = function () {
                setMessageInnerHTML("WebSocket连接成功");
            }

            //接收到消息的回调方法
            websocket.onmessage = function (event) {
                setMessageInnerHTML(event.data);
            }

            //连接关闭的回调方法
            websocket.onclose = function () {
                setMessageInnerHTML("WebSocket连接关闭");
            }
            document.getElementById('loginSpan').style.display = "none";
        }
        else {
            alert('当前浏览器 Not support websocket')
        }
    }
</script>
</html>