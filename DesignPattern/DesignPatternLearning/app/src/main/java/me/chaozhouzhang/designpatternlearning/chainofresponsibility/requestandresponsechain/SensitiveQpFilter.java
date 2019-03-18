package me.chaozhouzhang.designpatternlearning.chainofresponsibility.requestandresponsechain;

/**
 * Created on 2019/3/12 14:54
 *
 * @author zhangchaozhou
 */
public class SensitiveQpFilter implements QpFilter {
    @Override
    public void doFilter(Request req, Response resp, QpFilterChain chain) {

    }
}
