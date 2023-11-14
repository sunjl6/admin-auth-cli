package cn.sunjl.admin.authority.biz.service.common;

import cn.sunjl.admin.authority.entity.common.OptLog;
import cn.sunjl.admin.log.entity.OptLogDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OptLogService extends IService<OptLog> {
    void save(OptLogDTO optLogDTO);
}
