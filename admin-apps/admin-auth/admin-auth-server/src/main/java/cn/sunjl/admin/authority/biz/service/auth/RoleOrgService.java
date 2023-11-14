package cn.sunjl.admin.authority.biz.service.auth;
import java.util.List;

import cn.sunjl.admin.authority.entity.auth.RoleOrg;
import com.baomidou.mybatisplus.extension.service.IService;
/**
 * 业务接口
 * 角色组织关系
 */
public interface RoleOrgService extends IService<RoleOrg> {
    /**
     * 根据角色id查询
     */
    List<Long> listOrgByRoleId(Long id);
}
