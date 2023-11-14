package cn.sunjl.admin.authority.biz.service.auth;

import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.entity.auth.Resource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ResourceService extends IService<Resource> {
    List<Resource> findVisibleResource(ResourceQueryDTO resource);

    void removeByMenuId(List<Long> menuIds);

    List<Long> findMenuIdByResourceId(List<Long> resourceIdList);
}
