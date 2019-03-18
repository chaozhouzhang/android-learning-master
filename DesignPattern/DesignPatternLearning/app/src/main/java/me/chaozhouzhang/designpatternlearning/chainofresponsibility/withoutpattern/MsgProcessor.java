package me.chaozhouzhang.designpatternlearning.chainofresponsibility.withoutpattern;

/**
 * Created on 2019/3/12 13:41
 *
 * @author zhangchaozhou
 */
public class MsgProcessor {

    private String msg;

    public MsgProcessor(String msg) {
        this.msg = msg;
    }

    public String process() {
        String message = msg;
        message = message.replaceAll("<.*>", "");
        message = message.replace("被就业", "就业");
        return message;
    }
}
