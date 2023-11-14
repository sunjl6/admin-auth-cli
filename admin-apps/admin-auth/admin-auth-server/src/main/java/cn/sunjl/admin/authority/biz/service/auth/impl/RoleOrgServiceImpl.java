package cn.sunjl.admin.authority.biz.service.auth.impl;
import java.util.List;
import java.util.stream.Collectors;

import cn.sunjl.admin.authority.biz.dao.auth.RoleOrgMapper;
import cn.sunjl.admin.authority.biz.service.auth.RoleOrgService;
import cn.sunjl.admin.authority.entity.auth.RoleOrg;
import cn.sunjl.admin.database.mybatis.conditions.Wraps;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * 业务实现类
 * 角色组织关系
 */
@Slf4j
@Service
public class RoleOrgServiceImpl extends ServiceImpl<RoleOrgMapper, RoleOrg> implements RoleOrgService {
    @Override
    public List<Long> listOrgByRoleId(Long id) {
        List<RoleOrg> list = super.list(Wraps.<RoleOrg>lbQ().eq(RoleOrg::getRoleId, id));
        List<Long> orgList = list.stream().mapToLong(RoleOrg::getOrgId).boxed().collect(Collectors.toList());
        return orgList;
    }
}