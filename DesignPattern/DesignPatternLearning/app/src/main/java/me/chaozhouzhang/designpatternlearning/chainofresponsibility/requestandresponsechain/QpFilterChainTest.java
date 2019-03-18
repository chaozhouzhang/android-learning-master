package me.chaozhouzhang.designpatternlearning.chainofresponsibility.requestandresponsechain;

/**
 * Created on 2019/3/12 15:01
 *
 * @author zhangchaozhou
 */
public class QpFilterChainTest {
    public static void main(String[] args){

        String msg = "被就业了：)，<script>";


        Request req = new Request();
        Response resp = new Response();
        req.request = msg;
        resp.response = "response";
        QpFilterChain chain = new QpFilterChain();
        chain.addFilter(new HtmlQpFilter())
                .addFilter(new SensitiveQpFilter());
        chain.doFilter(req, resp);
        System.out.println(req.request);
        System.out.println(resp.response);

    }
}
