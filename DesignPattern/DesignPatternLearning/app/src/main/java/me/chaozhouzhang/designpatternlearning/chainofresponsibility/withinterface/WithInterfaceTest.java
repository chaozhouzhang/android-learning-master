package me.chaozhouzhang.designpatternlearning.chainofresponsibility.withinterface;

/**
 * Created on 2019/3/12 13:55
 *
 * @author zhangchaozhou
 */
public class WithInterfaceTest {

    public static void main(String[] args) {
        String msg = "被就业了：)，<script>";
        MsgFilterProcessor msgFilterProcessor = new MsgFilterProcessor(msg);
        System.out.println(msgFilterProcessor.process());
        System.out.println("如果需要过滤字符串中的其他信息，只需要创建一个类实现Filter接口，并在MsgFilterProcessor类中的filters字段中登记即可。");
    }
}
