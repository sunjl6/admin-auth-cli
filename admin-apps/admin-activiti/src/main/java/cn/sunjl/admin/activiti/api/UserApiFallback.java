package cn.sunjl.admin.activiti.api;

import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.authority.entity.auth.UserExt;
import cn.sunjl.admin.base.R;
import org.springframework.stereotype.Component;

@Component
public class UserApiFallback implements UserApi {

    @Override
    public R getUserByAccount(String account) {
        return null;
    }
}
