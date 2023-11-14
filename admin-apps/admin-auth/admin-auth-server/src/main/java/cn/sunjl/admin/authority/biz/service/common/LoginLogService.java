package cn.sunjl.admin.authority.biz.service.common;

import cn.sunjl.admin.authority.entity.common.LoginLog;
import com.baomidou.mybatisplus.extension.service.IService;

public interface LoginLogService extends IService<LoginLog> {
    LoginLog save(String account, String ua, String ip, String location, String description);
}
