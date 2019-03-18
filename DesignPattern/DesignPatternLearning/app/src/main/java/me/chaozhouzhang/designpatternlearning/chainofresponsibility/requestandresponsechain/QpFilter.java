package me.chaozhouzhang.designpatternlearning.chainofresponsibility.requestandresponsechain;

/**
 * Created on 2019/3/12 14:41
 *
 * @author zhangchaozhou
 */
public interface QpFilter {


    /**
     * 过滤
     *
     * @param req
     * @param resp
     * @param chain
     */
    void doFilter(Request req, Response resp, QpFilterChain chain);


}
