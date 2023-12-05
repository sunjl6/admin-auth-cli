package cn.sunjl.admin.zuul.api;

import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.base.R;
import org.springframework.stereotype.Component;

@Component
public class UserApiFallback implements UserApi{
    @Override
    public R<User> get(Long id) {
        return null;
    }
}
