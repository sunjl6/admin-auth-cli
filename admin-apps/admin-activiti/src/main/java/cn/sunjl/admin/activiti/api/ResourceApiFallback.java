package cn.sunjl.admin.activiti.api;

import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.entity.auth.Resource;
import cn.sunjl.admin.base.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 资源API熔断
 */
@Component
    public class ResourceApiFallback implements ResourceApi {
    public R<List> list() {
        return null;
    }

    public R<List<Resource>> visible(ResourceQueryDTO resource) {
        return null;
    }

    @Override
    public R visible2(ResourceQueryDTO resource) {
        return null;
    }
}
