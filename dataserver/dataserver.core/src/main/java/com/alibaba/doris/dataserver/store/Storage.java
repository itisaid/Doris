package com.alibaba.doris.dataserver.store;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface Storage {

    /**
     * 从存储中读取指定Key的Value值，如果数据不存在，则返回一个NullValue对象。
     * 
     * @param key
     * @return
     */
    public Value get(Key key);

    /**
     * 从存储中获取指定Key列表的值，返回一个Map对象保存所有对应的Value对象。<br>
     * 同get方法类似。
     * 
     * @param keyIterator
     * @return
     */
    public Map<Key, Value> getAll(Iterable<Key> keyIterator);

    /**
     * 保存指定Key，Value值到底层存储中，如果Key值已经在Storage中存在，则替换原有的值。<br>
     * 如果Key在Storage中不存在，则在新增并保存该值。
     * 
     * @param key
     * @param value
     */
    public void set(Key key, Value value);

    /**
     * 保存指定Key，Value值到底层存储当中,<br>
     * 同普通的set的方法相比，这个set命令可以设置版本号比较，<br>
     * 只有当传入参数Value的版本号比存储中的版本号新的情况下，<br>
     * SET操作才执行成功，否则驱动层会抛出一个版本冲突异常。
     * 
     * @param key
     * @param value
     * @param isSetWithCompare
     */
    public void set(Key key, Value value, boolean isSetWithCompareVersion);

    /**
     * 从存储层删除一个Key对应的值。
     * 
     * @param key
     */
    public boolean delete(Key key);

    /**
     * 从存储层删除一个Key对应的值。
     * 
     * @param key
     */
    public boolean delete(Key key, Value value);

    /**
     * 从存储层删除虚拟节点对应的所有数据。
     * 
     * @param key
     */
    public boolean delete(List<Integer> vnodeList);

    /**
     * 该迭代器实例每次迭代都会返回一个Pair对象，通过该对象可以获取对应的Key和Value对象。<br>
     * 注意，迭代器迭代过程中并不能准确实时的反应存储层的实际数据，<br>
     * 即迭代返回的数据有可能在随后的查询中已经被删除。
     * 
     * @return Iteraor<Pair>对象，标识一个遍历存储层的查询迭代器实例。
     */
    public Iterator<Pair> iterator();

    /**
     * 该迭代器实例每次迭代都会返回一个Pair对象，通过该对象可以获取对应的Key和Value对象。<br>
     * 注意，迭代器迭代过程中并不能准确实时的反应存储层的实际数据，<br>
     * 即迭代返回的数据有可能在随后的查询中已经被删除。
     * 
     * @param vnodeList 要遍历数据的虚拟节点编号列表；
     * @return Iteraor<Pair>对象，标识一个遍历存储层的查询迭代器实例。
     */
    public Iterator<Pair> iterator(List<Integer> vnodeList);

    /**
     * 打开一个存储，任何Storage都必须先Open后才能够调用具体的存取值等操作。
     */
    public void open();

    /**
     * 关闭一个Storage，当系统关闭时，需要调用此函数来确保存储正常关闭。<br>
     * 如果关闭Storage时未凋有Close方法，可能会造成缓存中的数据没有及时刷新到物理存储，<br>
     * 进而造成数据丢失。
     */
    public void close();

    /**
     * 获取当前存储的类型。
     * 
     * @return
     */
    public StorageType getType();

}
