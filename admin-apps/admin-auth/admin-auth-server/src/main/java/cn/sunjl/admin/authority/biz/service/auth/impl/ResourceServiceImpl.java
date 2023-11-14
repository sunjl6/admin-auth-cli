package cn.sunjl.admin.authority.biz.service.auth.impl;

import cn.sunjl.admin.authority.biz.dao.auth.ResourceMapper;
import cn.sunjl.admin.authority.biz.service.auth.ResourceService;
import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.entity.auth.Resource;
import cn.sunjl.admin.base.id.CodeGenerate;
import cn.sunjl.admin.common.constant.CacheKey;
import cn.sunjl.admin.database.mybatis.conditions.Wraps;
import cn.sunjl.admin.exception.BizException;
import cn.sunjl.admin.utils.StrHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @Autowired
    private CacheChannel cache;
    @Autowired
    private CodeGenerate codeGenerate;
    @Override
    public List<Resource> findVisibleResource(ResourceQueryDTO resourceQueryDTO) {
        //查询当前用户可访问的资源
        List<Resource> visibleResource =
                baseMapper.findVisibleResource(resourceQueryDTO);
        if(visibleResource != null && visibleResource.size() > 0){
            List<String> userResource = visibleResource.stream().map((Resource r) -> {
                        return r.getMethod() + r.getUrl();
                    }).collect(Collectors.toList());

            //将当前用户可访问的资源载入缓存，形式为：GET/user/page
            cache.set(CacheKey.USER_RESOURCE,
                    resourceQueryDTO.getUserId().toString(),
                    userResource);
        }
        return visibleResource;
    }

    @Override
    public void removeByMenuId(List<Long> menuIds) {
        List<Resource> resources = super.list(Wraps.<Resource>lbQ().in(Resource::getMenuId, menuIds));
        if (resources.isEmpty()) {
            return;
        }
        List<Long> idList = resources.stream().mapToLong(Resource::getId).boxed().collect(Collectors.toList());
        super.removeByIds(idList);
    }

    @Override
    public boolean save(Resource resource) {
        resource.setCode(StrHelper.getOrDef(resource.getCode(), codeGenerate.next()));
        if (super.count(Wraps.<Resource>lbQ().eq(Resource::getCode, resource.getCode())) > 0) {
            throw BizException.validFail("编码[%s]重复", resource.getCode());
        }
        super.save(resource);
        return true;
    }

    @Override
    public List<Long> findMenuIdByResourceId(List<Long> resourceIdList) {
        return baseMapper.findMenuIdByResourceId(resourceIdList);
    }
}
