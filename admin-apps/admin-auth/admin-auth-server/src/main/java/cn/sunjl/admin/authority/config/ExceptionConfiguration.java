package cn.sunjl.admin.authority.config;

import cn.sunjl.admin.common.handler.DefaultGlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class ExceptionConfiguration extends DefaultGlobalExceptionHandler {
}
