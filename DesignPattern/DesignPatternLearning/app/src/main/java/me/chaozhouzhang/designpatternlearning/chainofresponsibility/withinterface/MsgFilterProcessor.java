package me.chaozhouzhang.designpatternlearning.chainofresponsibility.withinterface;

/**
 * Created on 2019/3/12 13:56
 *
 * @author zhangchaozhou
 */
public class MsgFilterProcessor {

    private String msg;

    private Filter[] filters= new Filter[]{new HtmlFilter(),new SensitiveFilter()};

    public MsgFilterProcessor(String msg) {
        this.msg = msg;
    }

    public String process(){
        String message = msg;
        for (Filter filter :filters){
            message = filter.doFilter(message);
        }
        return message;
    }
}
