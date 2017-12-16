package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.dao.WeiboUserDao;
import cn.edu.zju.zjj.entity.WeiboUser;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 15:39
 */
@Service
public class WeiboUserService extends BaseService<WeiboUser>{

    @Autowired
    private WeiboUserService(WeiboUserDao weiboUserDao){
        super(weiboUserDao);
    }
}
