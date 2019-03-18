package me.chaozhouzhang.designpatternlearning.chainofresponsibility.requestandresponsechain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019/3/12 14:52
 *
 * @author zhangchaozhou
 */
public class QpFilterChain {
    private List<QpFilter> filters = new ArrayList<>();
    private int index = 0;
    public QpFilterChain addFilter(QpFilter f){
        filters.add(f);
        return this;
    }
    public void doFilter(Request req, Response resp) {
        if(index == filters.size()) {
            return;
        }
        //得到当前过滤器
        QpFilter f = filters.get(index);
        index++;
        f.doFilter(req, resp, this);
    }
}
