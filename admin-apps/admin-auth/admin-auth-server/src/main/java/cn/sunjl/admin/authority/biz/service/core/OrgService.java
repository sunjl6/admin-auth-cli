package cn.sunjl.admin.authority.biz.service.core;

import cn.sunjl.admin.authority.entity.core.Org;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface OrgService extends IService<Org> {
    /**
     * 查询指定id集合下的所有子集
     */
    List<Org> findChildren(List<Long> ids);
    // 更具pid 查询所有部门
    List<Org> getOrgsByPid(Long pid);

    //查询所有部门编码
    List<Org> getAbbreviation(String code);
    /**
     * 批量删除以及删除其子节点
     */
    boolean remove(List<Long> ids);
}
