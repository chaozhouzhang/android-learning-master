package me.chaozhouzhang.realtimedatalearning;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static me.chaozhouzhang.realtimedatalearning.MainActivity.APPID;

/**
 * Created on 2019/3/15 11:30
 *
 * @author zhangchaozhou
 */
public class Client {

    public static final String TAG = "BmobRealTimeData";
    public static final String ACTION_UPDATETABLE = "updateTable";
    public static final String ACTION_DELETETABLE = "deleteTable";
    public static final String ACTION_UPDATEROW = "updateRow";
    public static final String ACTION_DELETEROW = "deleteRow";

    /**
     * 数据监听服务器主机地址
     */
    public static final String DEFAULT_REAL_TIME_DATA_HOST = "http://io.bmob.cn:3010/socket.io/1/";
    /**
     * 数据监听websocket协议路径
     */
    public static final String DEFAULT_REAL_TIME_DATA_PATH_WEBSOCKET = "websocket/";
    /**
     * websocket协议
     */
    public static final String PROTOCOL_WEBSOCKET = "websocket";


    private OkHttpClient mOkHttpClient;
    private WebSocket mWebSocket;


    public Client() {
        mOkHttpClient = new OkHttpClient();
    }


    /**
     * 连接操作
     *
     * @param realTimeDataListener
     */
    public void connect(RealTimeDataListener realTimeDataListener) {

        /**
         * 获取服务器配置
         */
        getServerConfiguration(new OnGetServerConfigurationListener() {
            @Override
            public void onFailure(Exception ex) {
                realTimeDataListener.onConnectCompleted(Client.this, ex);
            }

            @Override
            public void onSuccess(int heartbeat, String session) {
                /**
                 * 新建会话
                 */
                newWebSocket(session, new OnNewWebsocketListener() {
                    @Override
                    public void onFailure(Exception ex) {
                        realTimeDataListener.onConnectCompleted(Client.this, ex);
                    }

                    @Override
                    public void onDisconnected() {
                        mWebSocket.send(String.format("0::%s", ""));
                    }

                    @Override
                    public void onConnected(WebSocket webSocket) {
                        mWebSocket = webSocket;
                        mWebSocket.send(String.format("1::%s", ""));
                        realTimeDataListener.onConnectCompleted(Client.this, null);
                    }

                    @Override
                    public void onHeartbeat() {

                        System.out.println("onHeartbeat");
                    }

                    @Override
                    public void onStringMessage() {

                    }

                    @Override
                    public void onJsonMessage() {

                    }

                    @Override
                    public void onChange(JSONObject jsonObject) {
                        System.out.println("onDataChange");
                        realTimeDataListener.onDataChange(Client.this, jsonObject);
                    }

                    @Override
                    public void onAck() {

                    }

                    @Override
                    public void onError(String err, String error) {

                        realTimeDataListener.onConnectCompleted(Client.this, new Exception(err + ":" + error));
                    }


                    @Override
                    public void onNoop() {

                    }


                });
            }
        });
    }


    /**
     * 获取服务器配置的回调
     */
    private interface OnGetServerConfigurationListener {

        /**
         * 获取服务器配置失败
         *
         * @param ex
         */
        void onFailure(Exception ex);

        /**
         * 获取服务器配置成功
         *
         * @param heartbeat
         * @param session
         */
        void onSuccess(int heartbeat, String session);
    }

    /**
     * 获取服务器配置信息的具体实现
     */
    public void getServerConfiguration(OnGetServerConfigurationListener onGetServerConfigurationListener) {
        Request.Builder builder = new Request.Builder();
        builder.url(DEFAULT_REAL_TIME_DATA_HOST);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), "");
        builder.post(requestBody);
        builder.header("User-Agent", "android-websockets-2.0");
        Request request = builder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onGetServerConfigurationListener.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                /**
                 * 规则：冒号分割
                 */
                String result = response.body().string();
                String[] parts = result.split(":");
                /**
                 * 会话
                 */
                String session = parts[0];
                /**
                 * 心跳
                 */
                String heartbeat = parts[1];
                /**
                 * 传输
                 */
                String transportsLine = parts[3];
                /**
                 * 心跳，默认为0，规则：/2*1000
                 */
                int heartbeatInt = 0;
                if (!"".equals(heartbeat)) {
                    heartbeatInt = Integer.parseInt(heartbeat) / 2 * 1000;
                }

                /**
                 * 传输协议，判断是否包含websocket协议传输，规则：逗号分割
                 */
                String[] transports = transportsLine.split(",");
                HashSet<String> set = new HashSet<>(Arrays.asList(transports));
                if (!set.contains(PROTOCOL_WEBSOCKET)) {
                    onGetServerConfigurationListener.onFailure(new Exception("websocket not supported"));
                    return;
                }

                /**
                 * 回调：返回心跳和会话
                 */
                onGetServerConfigurationListener.onSuccess(heartbeatInt, session);
            }
        });
    }


    private interface OnNewWebsocketListener {

        /**
         * 新建链接失败
         *
         * @param ex
         */
        void onFailure(Exception ex);


        /**
         * 0
         */
        void onDisconnected();

        /**
         * 1
         *
         * @param webSocket
         */
        void onConnected(WebSocket webSocket);


        /**
         * 2
         */
        void onHeartbeat();


        /**
         * 3
         */
        void onStringMessage();


        /**
         * 4
         */
        void onJsonMessage();


        /**
         * 5
         *
         * @param jsonObject
         */
        void onChange(JSONObject jsonObject);

        /**
         * 6
         */
        void onAck();

        /**
         * 7
         */
        void onError(String err, String error);


        /**
         * 8
         */
        void onNoop();

    }


    /**
     * 发起会话的实现
     *
     * @param session
     * @param onNewWebsocketListener
     */
    private void newWebSocket(String session, OnNewWebsocketListener onNewWebsocketListener) {
        Request.Builder builder = new Request.Builder();
        String key = createSecret();
        String url = DEFAULT_REAL_TIME_DATA_HOST + DEFAULT_REAL_TIME_DATA_PATH_WEBSOCKET + session;
        builder.url(url);
        builder.header("GET", "HTTP/1.1");
        builder.header("Upgrade", "websocket");
        builder.header("Connection", "Upgrade");
        builder.header("Host", "io.bmob.cn");
        builder.header("Origin", "http://io.bmob.cn");
        builder.header("User-Agent", "android-websockets-2.0");
        builder.header("Sec-WebSocket-Key", key);
        builder.header("Sec-WebSocket-Version", "13");
        Request request = builder.build();
        mOkHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                System.out.println("onOpen：" + response.message());

                Headers headers = response.headers();
                System.out.println("onOpen：" + headers.toString());
                try {
                    System.out.println("onOpen：" + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String expected = expectedKey(key);
                System.out.println("onOpen：" + expected);


            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);

                try {


                    System.out.println("onMessage：" + text);
                    String[] parts = text.split(":", 4);
                    int code = Integer.parseInt(parts[0]);
                    switch (code) {
                        case 0:
                            // disconnect
//                    webSocketClient.disconnect();
//                    reportDisconnect(null);

                            onNewWebsocketListener.onDisconnected();
                            break;
                        case 1:
                            // connect
//                    reportConnect(parts[2]);
                            onNewWebsocketListener.onConnected(webSocket);
                            break;
                        case 2:
                            // heartbeat
                            send("2::");
                            onNewWebsocketListener.onHeartbeat();
                            break;
                        case 3: {
                            // message
//                    reportString(parts[2], parts[3], acknowledge(parts[1]));
                            onNewWebsocketListener.onStringMessage();
                            break;
                        }
                        case 4: {
                            // json message
                            final String dataString = parts[3];
                            final JSONObject jsonMessage = new JSONObject(dataString);
//                    reportJson(parts[2], jsonMessage, acknowledge(parts[1]));
                            onNewWebsocketListener.onJsonMessage();

                            break;
                        }
                        case 5: {
                            final String dataString = parts[3];
                            final JSONObject data = new JSONObject(dataString);
                            final String event = data.getString("name");
                            final JSONArray args = data.optJSONArray("args");
                            System.out.println(event);
                            if (event.equals("server_pub")) {
                                System.out.println(event);
                                String json = args.getString(0);
                                onNewWebsocketListener.onChange(new JSONObject(json));
                            }
                            break;
                        }
                        case 6:
                            // ACK
                            final String[] ackParts = parts[3].split("\\+", 2);
                            JSONArray arguments = null;
                            if (ackParts.length == 2) {
                                arguments = new JSONArray(ackParts[1]);
                            }

                            String data = "";
                            if (arguments != null) {
                                data += "+" + arguments.toString();
                            }
                            webSocket.send(String.format("6:::%s%s", parts[1], data));
                            break;
                        case 7:
                            // error
                            onNewWebsocketListener.onError(parts[2], parts[3]);
                            break;
                        case 8:
                            // noop
                            break;
                        default:
                            throw new Exception("unknown code");
                    }
                } catch (Exception e) {
                    //TODO :handle exception
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                System.out.println("onClosing：" + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                System.out.println("onClosed：" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                System.out.println("onFailure：" + t.getMessage());
                onNewWebsocketListener.onFailure(new Exception(t));
            }
        });
    }


    private String createSecret() {
        byte[] nonce = new byte[16];
        for (int i = 0; i < 16; i++) {
            nonce[i] = (byte) (Math.random() * 256);
        }
        return Base64.encodeToString(nonce, Base64.DEFAULT).trim();
    }


    private String expectedKey(String secret) {
        //concatenate, SHA1-hash, base64-encode
        try {
            final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            final String secretGUID = secret + GUID;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(secretGUID.getBytes());
            return Base64.encodeToString(digest, Base64.DEFAULT).trim();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }


    int ackCount = 0;

    public void emitRaw(int type, String message) {
        String id = "" + ackCount++;
        String ack = id + "+";
        send(String.format("%d:%s:%s:%s", type, ack, "", message));
    }

    public void send(String text) {
        System.out.println("send：" + text);
        mWebSocket.send(text);
    }


    /**
     * 监听表数据更新
     *
     * @param tableName 监听的表名
     */
    public void subTableUpdate(String tableName) {
        JSONArray args = new JSONArray();
        args.put(getArgs(tableName, "", ACTION_UPDATETABLE).toString());
        emit("client_sub", args);
    }

    /**
     * 取消监听表数据更新
     *
     * @param tableName 取消监听的表名
     */
    public void unsubTableUpdate(String tableName) {
        JSONArray args = new JSONArray();
        args.put(getArgs(tableName, "", "unsub_updateTable").toString());
        emit("client_unsub", args);
    }

    /**
     * 监听表删除
     *
     * @param tableName 监听的表名
     */
    public void subTableDelete(String tableName) {
        JSONArray args = new JSONArray();
        args.put(getArgs(tableName, "", ACTION_DELETETABLE).toString());
        emit("client_sub", args);
    }

    /**
     * 取消监听表删除
     *
     * @param tableName 取消监听的表名
     */
    public void unsubTableDelete(String tableName) {
        JSONArray args = new JSONArray();
        args.put(getArgs(tableName, "", "unsub_deleteTable").toString());
        emit("client_unsub", args);
    }

    /**
     * 监听行数据更新
     *
     * @param tableName 监听的表名
     * @param objectId  监听的行Id
     */
    public void subRowUpdate(String tableName, String objectId) {
        JSONArray args = new JSONArray();
        args.put(getArgs(tableName, objectId, ACTION_UPDATEROW).toString());
        emit("client_sub", args);
    }

    /**
     * 取消监听行数据更新
     *
     * @param tableName 取消监听的表名
     * @param objectId  取消监听的行Id
     */
    public void unsubRowUpdate(String tableName, String objectId) {
        JSONArray args = new JSONArray();
        args.put(getArgs(tableName, objectId, "unsub_updateRow").toString());
        emit("client_unsub", args);
    }

    /**
     * 监听数据行删除
     *
     * @param tableName 监听的表名
     * @param objectId  监听的行Id
     */
    public void subRowDelete(String tableName, String objectId) {
        JSONArray args = new JSONArray();
        args.put(getArgs(tableName, objectId, ACTION_DELETEROW).toString());
        emit("client_sub", args);
    }

    /**
     * 取消监听数据行删除
     *
     * @param tableName 取消监听的表名
     * @param objectId  取消监听的行Id
     */
    public void unsubRowDelete(String tableName, String objectId) {
        JSONArray args = new JSONArray();
        args.put(getArgs(tableName, objectId, "unsub_deleteRow").toString());
        emit("client_unsub", args);
    }

    private JSONObject getArgs(String tableName, String objectId, String action) {
        JSONObject j = new JSONObject();
        try {
            j.put("appKey", APPID);
            j.put("tableName", tableName);
            j.put("objectId", objectId);
            j.put("action", action);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }


    public void emit(String name, JSONArray args) {
        final JSONObject event = new JSONObject();
        try {
            event.put("name", name);
            event.put("args", args);
            emitRaw(5, event.toString());
        } catch (Exception e) {
        }
    }

}
