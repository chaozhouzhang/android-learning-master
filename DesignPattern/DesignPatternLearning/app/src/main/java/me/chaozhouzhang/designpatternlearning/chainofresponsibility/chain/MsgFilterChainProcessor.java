package me.chaozhouzhang.designpatternlearning.chainofresponsibility.chain;

/**
 * Created on 2019/3/12 14:08
 *
 * @author zhangchaozhou
 */
public class MsgFilterChainProcessor {

    private String msg;

    private FilterChain mFilterChain;


    public MsgFilterChainProcessor(String msg, FilterChain filterChain) {
        this.msg = msg;
        mFilterChain = filterChain;
    }

    public String process(){
        return mFilterChain.doFilter(msg);
    }
}
