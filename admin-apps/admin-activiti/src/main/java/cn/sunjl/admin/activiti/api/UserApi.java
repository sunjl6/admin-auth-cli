package cn.sunjl.admin.activiti.api;

import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.authority.entity.auth.UserExt;
import cn.sunjl.admin.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@FeignClient(name = "${admin.feign.authority-server:admin-auth-server}",fallback = UserApiFallback.class)
public interface UserApi {

    @GetMapping("/user/getUserByAccount/{account}")
    public R<UserExt> getUserByAccount(@PathVariable String account);
}
