package cn.sunjl.admin.authority.biz.service.core.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.sunjl.admin.authority.biz.dao.core.OrgMapper;
import cn.sunjl.admin.authority.biz.service.core.OrgService;
import cn.sunjl.admin.authority.entity.core.Org;
import cn.sunjl.admin.database.mybatis.conditions.Wraps;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrgServiceImpl extends ServiceImpl<OrgMapper, Org> implements OrgService {
    @Override
    public List<Org> findChildren(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // MySQL 全文索引
        String applySql = String.format(" MATCH(tree_path) AGAINST('%s' IN BOOLEAN MODE) ", StringUtils.join(ids, " "));
        return super.list(Wraps.<Org>lbQ().in(Org::getId, ids).or(query -> query.apply(applySql)));
    }

    @Override
    public List<Org> getOrgsByPid(Long pid) {
        QueryWrapper<Org> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", pid);
        List<Org> orgList = baseMapper.selectList(wrapper);
        return orgList;
    }

    @Override
    public List<Org> getAbbreviation(String code) {
        QueryWrapper<Org> wrapper = new QueryWrapper<>();
        wrapper.eq("abbreviation",code);
        List<Org> orgs = baseMapper.selectList(wrapper);
        return orgs;
    }

    @Override
    public boolean remove(List<Long> ids) {
        List<Org> list = this.findChildren(ids);
        List<Long> idList = list.stream().mapToLong(Org::getId).boxed().collect(Collectors.toList());
        return !idList.isEmpty() ? super.removeByIds(idList) : true;
    }
}
