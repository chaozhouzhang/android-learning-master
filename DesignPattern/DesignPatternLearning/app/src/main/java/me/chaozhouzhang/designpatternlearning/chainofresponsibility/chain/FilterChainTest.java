package me.chaozhouzhang.designpatternlearning.chainofresponsibility.chain;

import me.chaozhouzhang.designpatternlearning.chainofresponsibility.withinterface.HtmlFilter;
import me.chaozhouzhang.designpatternlearning.chainofresponsibility.withinterface.SensitiveFilter;

/**
 * Created on 2019/3/12 14:07
 *
 * @author zhangchaozhou
 */
public class FilterChainTest {
    public static void main(String[] args) {
        String msg = "被就业了：)，<script>";
        FilterChain filterChain = new FilterChain();
        filterChain.addFilter(new HtmlFilter());
        filterChain.addFilter(new SensitiveFilter());
        MsgFilterChainProcessor msgFilterChainProcessor = new MsgFilterChainProcessor(msg, filterChain);
        System.out.println(msgFilterChainProcessor.process());
        System.out.println("将一个事件处理流程分派到一组执行对象上去，这一组执行对象形成一个链式结构，事件处理请求在这一组执行对象上进行传递。");
    }
}
