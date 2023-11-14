package cn.sunjl.admin.authority.config;

import cn.sunjl.admin.authority.biz.service.common.OptLogService;
import cn.sunjl.admin.log.entity.OptLogDTO;
import cn.sunjl.admin.log.event.SysLogListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.function.Consumer;

@Configuration
@EnableAsync
public class SysLogConfiguration {
    @Bean
    public SysLogListener sysLogListener(OptLogService optLogService){
        Consumer<OptLogDTO> consumer = (optLogDTO)->optLogService.save(optLogDTO);
        return new SysLogListener(consumer);
    }
}
