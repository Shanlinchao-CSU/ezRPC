package com.example.rpcframework.filter;

import java.util.ArrayList;
import java.util.List;

public class FilterChain {
    private List<Filter> filters = new ArrayList<>();

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public void addFilter(List<Object> list) {
        for (Object filter:list) {
            filters.add((Filter)filter);
        }
    }

    public void doFilter(FilterData filterData) {
        for (Filter filter:filters) {
            filter.doFilter(filterData);
        }
    }
}
