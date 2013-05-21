package com.alibaba.doris.dataserver.core;

/**
 * 实现该接口需要注意线程安全的问题，目前提供的默认实现是非线程安全的。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface RequestFilterChian {

    void doFilter(Request request, Response response);

    /**
     * 往FilterChain中增加一个Filter
     * 
     * @param filter
     */
    public void addFilter(RequestFilter filter);

    /**
     * 往FilterChain的最后位置插入一个Filter，如果FilterChain中已经存在过类似的Filter<br>
     * 该方法会抛出异常，表示最后位置的Filter已经被占用。
     * 
     * @param filter
     */
    public void addLastFilter(RequestFilter filter);

    /**
     * 声明往FilterChain的起始位置插入一个Filter，如果FilterChain中已经存在一个类似的<br>
     * Filter，方法会抛出异常，标识其实位置已经被占用。
     * 
     * @param filter
     */
    public void addFirstFilter(RequestFilter filter);

    /**
     * 往FilterChain插入一个Filter，并且声明该Filter必须在指定的currentFilter位置之后。<br>
     * 注意，如果最后一个元素声明为Last Filter，并且调用者希望新插入的Filter为Last Filter<br>
     * 后，则方法会抛出异常。
     * 
     * @param currentFilter 当前已经存在于FilterChain中的一个Filter实例。
     * @param filter 待插入FilterChain中的一个Filter实例，函数执行成功后，filter一定<br>
     * 在currentFilter之后。
     */
    public void addFilterAfter(RequestFilter currentFilter, RequestFilter filter);

    /**
     * 根据名称（Filter的Class名称）获取FilterChain中的Filter对象实例，<br>
     * 如果存在返回对象实例，否则返回null;
     * 
     * @param requestFilterName
     * @return
     */
    public RequestFilter getRequestFilter(String requestFilterName);
}
