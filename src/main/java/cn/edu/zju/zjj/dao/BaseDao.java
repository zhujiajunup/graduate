package cn.edu.zju.zjj.dao;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 15:05
 */
public interface BaseDao<T> {
    void insert(T t);
    void update(T t);
    boolean exist(String id);

}
