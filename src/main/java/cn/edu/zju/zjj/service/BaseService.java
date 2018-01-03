package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.App;
import cn.edu.zju.zjj.dao.BaseDao;
import cn.edu.zju.zjj.entity.BaseEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 18:59
 */
@Slf4j
public abstract class BaseService<T extends BaseEntity> {
    private BaseDao<T> dao;

    public BaseService(BaseDao<T> dao){
        this.dao = dao;
    }

    public void insertOrUpdate(T t){
        if(dao.exist(t.getId())){
            log.info("update entity: {}", t);

            dao.update(t);
            App.update.incrementAndGet();
        }else{
            log.info("insert entity: {}", t);

            dao.insert(t);
            App.insert.incrementAndGet();
        }
    }
}
