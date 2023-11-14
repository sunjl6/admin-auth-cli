package cn.sunjl.admin.authority.biz.service.common.impl;

import cn.sunjl.admin.authority.biz.dao.common.LoginLogMapper;
import cn.sunjl.admin.authority.biz.service.common.LoginLogService;
import cn.sunjl.admin.authority.entity.common.LoginLog;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
    @Override
    public LoginLog save(String account, String ua, String ip, String location, String description) {
        LoginLog loginLog = new LoginLog();
        loginLog.setAccount(account);
        loginLog.setUa(ua);
        loginLog.setRequestIp(ip);
        loginLog.setLocation(location);
        loginLog.setDescription(description);
        baseMapper.insert(loginLog);
        return null;
    }
}
