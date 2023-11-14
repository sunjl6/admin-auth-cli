package cn.sunjl.admin.authority.biz.service.auth;
import java.util.List;

import cn.sunjl.admin.authority.dto.auth.RoleSaveDTO;
import cn.sunjl.admin.authority.dto.auth.RoleUpdateDTO;
import cn.sunjl.admin.authority.entity.auth.Role;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 业务接口
 * 角色
 */
public interface RoleService extends IService<Role> {
    /**
     * 根据ID删除
     */
    boolean removeById(List<Long> ids);

    /**
     * 查询用户拥有的角色
     */
    List<Role> findRoleByUserId(Long userId);

    /**
     * 保存角色
     */
    void saveRole(RoleSaveDTO data, Long userId);

    /**
     * 修改
     */
    void updateRole(RoleUpdateDTO role, Long userId);

    /**
     * 根据角色编码查询用户ID
     */
    List<Long> findUserIdByCode(String[] codes);

    /**
     * 检测编码重复 存在返回真
     */
    Boolean check(String code);
}
