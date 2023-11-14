package cn.sunjl;

import cn.sunjl.admin.validator.config.EnableFormValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFormValidator
public class MyTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyTestApplication.class,args);
    }
}
