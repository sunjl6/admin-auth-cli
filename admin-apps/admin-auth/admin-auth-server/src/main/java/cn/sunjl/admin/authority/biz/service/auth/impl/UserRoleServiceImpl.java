package cn.sunjl.admin.authority.biz.service.auth.impl;
import cn.sunjl.admin.authority.biz.dao.auth.UserRoleMapper;
import cn.sunjl.admin.authority.biz.service.auth.UserRoleService;
import cn.sunjl.admin.authority.entity.auth.UserRole;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * 业务实现类
 * 角色分配
 * 账号角色绑定
 */
@Slf4j
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}