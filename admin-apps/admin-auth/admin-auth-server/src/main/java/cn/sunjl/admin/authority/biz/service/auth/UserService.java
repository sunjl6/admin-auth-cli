package cn.sunjl.admin.authority.biz.service.auth;

import cn.sunjl.admin.authority.dto.auth.RoleSaveDTO;
import cn.sunjl.admin.authority.dto.auth.RoleUpdateDTO;
import cn.sunjl.admin.authority.dto.auth.UserSaveDTO;
import cn.sunjl.admin.authority.dto.auth.UserUpdatePasswordDTO;
import cn.sunjl.admin.authority.entity.auth.Role;
import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.database.mybatis.conditions.query.LbqWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserService extends IService<User> {
    /**
     * 根据角色id 和 账号或名称 查询角色关联的用户
     * 注意，该接口只返回 id，账号，姓名，手机，性别
     * @param roleId  角色id
     * @param keyword 账号或名称
     */
    List<User> findUserByRoleId(Long roleId, String keyword);



    /**
     * 修改输错密码的次数
     */
    void updatePasswordErrorNumById(Long id);

    /**
     * 根据账号查询用户
     */
    User getByAccount(String account);

    /**
     * 修改用户最后一次登录 时间
     */
    void updateLoginTime(String account);

    /**
     * 保存
     */
    User saveUser(User user);

    /**
     * 重置密码
     */
    boolean reset(List<Long> ids);

    /**
     * 修改
     */
    User updateUser(User user);

    /**
     * 删除
     */
    boolean remove(List<Long> ids);

    /**
     * 分页
     */
    IPage<User> findPage(IPage<User> page, LbqWrapper<User> wrapper);

    /**
     * 修改密码
     */
    Boolean updatePassword(UserUpdatePasswordDTO data);

    /**
     * 重置密码错误次数
     */
    int resetPassErrorNum(Long id);
}
