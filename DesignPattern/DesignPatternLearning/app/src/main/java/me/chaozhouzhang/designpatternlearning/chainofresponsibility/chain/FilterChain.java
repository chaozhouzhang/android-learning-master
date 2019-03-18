package me.chaozhouzhang.designpatternlearning.chainofresponsibility.chain;

import java.util.ArrayList;
import java.util.List;

import me.chaozhouzhang.designpatternlearning.chainofresponsibility.withinterface.Filter;

/**
 * Created on 2019/3/12 14:05
 *
 * @author zhangchaozhou
 */
public class FilterChain implements Filter {


    public List<Filter> mFilters = new ArrayList<>();


    public FilterChain addFilter(Filter filter) {
        mFilters.add(filter);
        return this;
    }


    @Override
    public String doFilter(String msg) {
        String message = msg;
        for (Filter filter : mFilters) {
            message = filter.doFilter(message);
        }
        return message;
    }
}
