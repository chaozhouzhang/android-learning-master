package me.chaozhouzhang.designpatternlearning.chainofresponsibility.withoutpattern;

/**
 * Created on 2019/3/12 13:32
 *
 * @author zhangchaozhou
 */
public class WithoutPatternTest {

    public static void main(String[] args) {
        String msg = "被就业了：)，<script>";
        MsgProcessor msgProcessor = new MsgProcessor(msg);
        System.out.println(msgProcessor.process());
        System.out.println("对字符串的处理都放在MsgProcessor类的process方法中，扩展性差！");
    }
}
