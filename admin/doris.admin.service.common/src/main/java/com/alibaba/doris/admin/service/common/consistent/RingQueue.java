package com.alibaba.doris.admin.service.common.consistent;


/**
 * 类RingQueue.java的实现描述：环形队列，队列满了队首元素弹出，队尾插入<br/>
 * 注意：非线程安全
 * 
 * @author hongwei.zhaohw 2012-1-19
 */
public class RingQueue<T> {

    private static final int DEFAULT_CAPACITY = 100;
    private T[]              datas;
    private int              head             = 0;
    private int              tail             = 0;
    private int              size             = 0;
    private int              capacity         = DEFAULT_CAPACITY;

    public RingQueue() {
        this(DEFAULT_CAPACITY);
    }

    public RingQueue(int capacity) {
        this.capacity = capacity;
        datas = (T[]) new Object[capacity];
    }

    /**
     * 队尾插入
     * 
     * @param data
     */
    public void put(T data) {
        if (datas[tail] != null) {
            head++;
        }

        datas[tail] = data;
        tail = ++tail % capacity;
        if (size++ >= capacity) {
            size = capacity;
        }
    }

    /**
     * 从队首取出
     * 
     * @return
     */
    public T get() {
        if (size <= 0) {
            return null;
        }

        T data = datas[head];
        datas[head] = null;
        head = ++head % capacity;
        size--;
        return data;
    }

    /**
     * 一次取出所有元素
     * 
     * @param copy
     * @return
     */
    public <T> T[] getAll(T[] copy) {
        int index = 0;
        for (int i = 0; i < capacity; i++) {
            if (datas[i] != null) {
                copy[index++] = (T) datas[i];
                datas[i] = null;
            }
        }
        return copy;
    }

    public int getSize() {
        return size;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < capacity; i++) {
            sb.append(String.valueOf(datas[i])).append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        RingQueue<String> q = new RingQueue<String>(5);
        q.put("s1");
        q.put("s2");
        q.put("s3");
        System.out.println(q.tail);
        System.out.println(q);
        System.out.println(q.get());
        System.out.println(q);
        q.put("s4");
        q.put("s5");
        q.put("s6");
        q.put("s7");
        System.out.println(q);
        q.get();
        System.out.println(q);
        q.put("s8");
        q.put("s9");
        q.get();
        q.put("s10");
        q.put("s11");
        q.put("s12");
        q.get();
        System.out.println(q);
        String[] sCopy = new String[q.size];
        String[] all = q.getAll(sCopy);
        for (int i = 0; i < all.length; i++) {
            System.out.print(all[i] + ",");
        }
        System.out.println(q);
    }
}
