package cn.sunjl.admin.zuul.api;

import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static cn.sunjl.admin.context.BaseContextConstants.JWT_KEY_USER_ID;

@FeignClient(name = "${admin.feign.authority-server:admin-auth-server}",fallback = UserApiFallback.class)
public interface UserApi {

    @GetMapping("/{id}")
    public R<User> get(@PathVariable Long id);
}
