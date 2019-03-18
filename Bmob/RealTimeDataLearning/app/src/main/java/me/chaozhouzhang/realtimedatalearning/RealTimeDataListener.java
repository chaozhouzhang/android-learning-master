package me.chaozhouzhang.realtimedatalearning;

import org.json.JSONObject;

/**
 * Created on 2019/3/14 13:18
 *
 * @author zhangchaozhou
 */
public interface RealTimeDataListener {


    /**
     * 连接监听
     * @param ex
     * @param client
     */
    void onConnectCompleted(Client client,Exception ex);

    /**
     * 数据变化的监听
     * @param data
     * @param client
     */
    void onDataChange(Client client,JSONObject data);


}
