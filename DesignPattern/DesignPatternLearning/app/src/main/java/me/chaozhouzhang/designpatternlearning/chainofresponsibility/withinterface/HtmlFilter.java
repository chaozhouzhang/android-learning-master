package me.chaozhouzhang.designpatternlearning.chainofresponsibility.withinterface;

/**
 * Created on 2019/3/12 13:51
 *
 * @author zhangchaozhou
 */
public class HtmlFilter implements Filter {
    @Override
    public String doFilter(String msg) {
        return msg.replaceAll("<.*>", "");
    }
}
