package cn.sunjl.admin.activiti;
import cn.sunjl.admin.jwt.server.EnableAuthServer;
import cn.sunjl.admin.user.annotation.EnableLoginArgResolver;
import cn.sunjl.admin.validator.config.EnableFormValidator;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class})
//@SpringBootApplication
@EnableDiscoveryClient
@EnableAuthServer
@EnableFeignClients(value = {
        "cn.sunjl.admin",
})
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableLoginArgResolver
@EnableFormValidator
@MapperScan(basePackages = "cn.sunjl.admin.activiti.dao")
public class ActivitiApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application =
       SpringApplication.run(ActivitiApplication.class,args);
        Environment env = application.getEnvironment();
        log.info("应用 '{}' 运行成功!  Swagger文档: http://{}:{}/doc.html",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }
}
