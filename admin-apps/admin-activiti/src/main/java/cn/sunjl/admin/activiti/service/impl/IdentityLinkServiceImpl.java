package cn.sunjl.admin.activiti.service.impl;

import cn.sunjl.admin.activiti.dao.IdentityLinkMapper;
import cn.sunjl.admin.activiti.pojo.IdentityLink;
import cn.sunjl.admin.activiti.service.IdentityLinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IdentityLinkServiceImpl extends ServiceImpl<IdentityLinkMapper, IdentityLink> implements IdentityLinkService {
}
