package cn.sunjl.admin.log.event;


import cn.sunjl.admin.log.entity.OptLogDTO;
import org.springframework.context.ApplicationEvent;

/**
 * 系统日志事件
 *
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(OptLogDTO source) {
        super(source);
    }
}
