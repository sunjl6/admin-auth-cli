package cn.sunjl.admin.authority.biz.service.auth;
import cn.sunjl.admin.authority.dto.auth.RoleAuthoritySaveDTO;
import cn.sunjl.admin.authority.dto.auth.UserRoleSaveDTO;
import cn.sunjl.admin.authority.entity.auth.RoleAuthority;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 业务接口
 * 角色的资源
 */
public interface RoleAuthorityService extends IService<RoleAuthority> {
    /**
     * 给用户分配角色
     */
    boolean saveUserRole(UserRoleSaveDTO userRole);

    /**
     * 给角色重新分配 权限（资源/菜单）
     */
    boolean saveRoleAuthority(RoleAuthoritySaveDTO roleAuthoritySaveDTO);
}
