package cn.sunjl.admin.authority.biz.dao.auth;

import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.entity.auth.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ResourceMapper extends BaseMapper<Resource> {
    List<Resource> findVisibleResource(ResourceQueryDTO resource);

    List<Long> findMenuIdByResourceId(@Param("resourceIdList") List<Long> resourceIdList);
}
