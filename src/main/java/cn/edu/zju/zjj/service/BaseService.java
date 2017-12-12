package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.dao.BaseDao;
import cn.edu.zju.zjj.entity.BaseEntity;
import com.sun.xml.internal.rngom.parse.host.Base;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 18:59
 */
public abstract class BaseService<T extends BaseEntity> {
    private BaseDao<T> dao;

    public BaseService(BaseDao<T> dao){
        this.dao = dao;
    }

    public void insertOrUpdate(T t){
        if(dao.exist(t.getId())){
            dao.update(t);
        }else{
            dao.insert(t);
        }
    }
}
