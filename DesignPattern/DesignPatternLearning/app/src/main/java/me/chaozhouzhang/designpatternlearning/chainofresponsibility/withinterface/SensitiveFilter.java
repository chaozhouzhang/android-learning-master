package me.chaozhouzhang.designpatternlearning.chainofresponsibility.withinterface;

/**
 * Created on 2019/3/12 13:54
 *
 * @author zhangchaozhou
 */
public class SensitiveFilter implements Filter {
    @Override
    public String doFilter(String msg) {
        return msg.replace("被就业", "就业");
    }
}
