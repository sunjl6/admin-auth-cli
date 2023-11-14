package cn.sunjl.admin.authority.biz.service.common.impl;

import cn.sunjl.admin.authority.biz.dao.common.OptLogMapper;
import cn.sunjl.admin.authority.biz.service.common.OptLogService;
import cn.sunjl.admin.authority.entity.common.OptLog;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.log.entity.OptLogDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OptLogServiceImpl extends ServiceImpl<OptLogMapper, OptLog> implements OptLogService {
    @Autowired
    private DozerUtils dozerUtils;
    @Override
    public void save(OptLogDTO optLogDTO) {
        baseMapper.insert(dozerUtils.map(optLogDTO,OptLog.class));
    }
}
