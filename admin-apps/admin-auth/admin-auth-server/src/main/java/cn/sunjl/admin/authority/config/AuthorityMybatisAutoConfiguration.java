package cn.sunjl.admin.authority.config;

import cn.sunjl.admin.database.datasource.BaseMybatisConfiguration;
import cn.sunjl.admin.database.properties.DatabaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AuthorityMybatisAutoConfiguration extends BaseMybatisConfiguration {
    public AuthorityMybatisAutoConfiguration(DatabaseProperties databaseProperties) {
        super(databaseProperties);
    }
}
