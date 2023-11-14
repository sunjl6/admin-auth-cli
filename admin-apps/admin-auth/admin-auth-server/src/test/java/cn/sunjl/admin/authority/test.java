package cn.sunjl.admin.authority;

import cn.sunjl.admin.jwt.server.utils.JwtTokenServerUtils;
import cn.sunjl.admin.jwt.utils.JwtUserInfo;
import cn.sunjl.admin.jwt.utils.Token;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class test {
    @Autowired
    private JwtTokenServerUtils jwtTokenServerUtils;
    @Test
    public void test1(){
        Token token = jwtTokenServerUtils.generateUserToken(new JwtUserInfo(2L, "sjl", "abcd,1234", 23L, 12L), 8000);
        System.out.println("token"+token);
    }

}
