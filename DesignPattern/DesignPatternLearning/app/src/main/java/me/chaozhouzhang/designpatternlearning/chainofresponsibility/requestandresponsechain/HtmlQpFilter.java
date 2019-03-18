package me.chaozhouzhang.designpatternlearning.chainofresponsibility.requestandresponsechain;

/**
 * Created on 2019/3/12 14:40
 *
 * @author zhangchaozhou
 */
public class HtmlQpFilter implements QpFilter {

    @Override
    public void doFilter(Request req, Response resp, QpFilterChain chain) {
        req.request = req.request.replace("<", "<").replace(">", ">");
        req.request += "---HtmlFilter()---";
        chain.doFilter(req, resp);
        resp.response += "---HtmlFilter()---";
    }
}
