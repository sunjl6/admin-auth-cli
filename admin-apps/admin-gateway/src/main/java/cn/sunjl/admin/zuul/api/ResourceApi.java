package cn.sunjl.admin.zuul.api;

import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.entity.auth.Resource;
import cn.sunjl.admin.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@FeignClient(name = "${admin.feign.authority-server:admin-auth-server}",fallback = ResourceApiFallback.class)
//@FeignClient(name = "${admin.feign.authority-server:admin-auth-server}")
public interface ResourceApi {
    //获取所有需要鉴权的资源
    @GetMapping("/resource/list")
    public R<List> list();

    //查询当前登录用户拥有的资源权限
    @PostMapping("/resource/getUserAllRes")
    public R<List<Resource>> visible(@RequestBody ResourceQueryDTO resource);

    @GetMapping("/resource/testFeign")
    public R visible2(@RequestParam ResourceQueryDTO resource);
}
