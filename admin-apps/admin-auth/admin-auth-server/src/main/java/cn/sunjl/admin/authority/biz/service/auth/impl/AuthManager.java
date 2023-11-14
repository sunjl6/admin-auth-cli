package cn.sunjl.admin.authority.biz.service.auth.impl;

import cn.sunjl.admin.authority.biz.service.auth.ResourceService;
import cn.sunjl.admin.authority.biz.service.auth.UserService;
import cn.sunjl.admin.authority.biz.service.common.LoginLogService;
import cn.sunjl.admin.authority.dto.auth.LoginDTO;
import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.dto.auth.UserDTO;
import cn.sunjl.admin.authority.entity.auth.Resource;
import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.exception.code.ExceptionCode;
import cn.sunjl.admin.jwt.server.utils.JwtTokenServerUtils;
import cn.sunjl.admin.jwt.utils.JwtUserInfo;
import cn.sunjl.admin.jwt.utils.Token;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthManager {
    @Autowired
    private JwtTokenServerUtils jwtTokenServerUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private DozerUtils dozer;
    /**
     * 账号登录
     * @param account
     * @param password
     */
    public R<LoginDTO> login(String account, String password) {
        // 登录验证
        R<User> result = checkUser(account, password);
        if (result.getIsError()) {
            return R.fail(result.getCode(), result.getMsg());
        }
        User user = result.getData();

        // 生成jwt token
        Token token = this.generateUserToken(user);

        List<Resource> resourceList =this.resourceService.
                findVisibleResource(ResourceQueryDTO.builder().
                        userId(user.getId()).build());
        List<String> permissionsList = null;
        if(resourceList != null && resourceList.size() > 0){
            permissionsList = resourceList.stream().
                    map(Resource::getCode).
                    collect(Collectors.toList());
        }
        //封装数据
        LoginDTO loginDTO = LoginDTO.builder()
                .user(this.dozer.map(user, UserDTO.class))
                .token(token)
                .permissionsList(permissionsList)
                .build();
        return R.success(loginDTO);
    }
    //生成jwt token
    private Token generateUserToken(User user) {
        JwtUserInfo userInfo = new JwtUserInfo(user.getId(),
                user.getAccount(),
                user.getName(),
                user.getOrgId(),
                user.getStationId());

        Token token = this.jwtTokenServerUtils.generateUserToken(userInfo, null);
        log.info("token={}", token.getToken());
        return token;
    }

    // 登录验证
    private R<User> checkUser(String account, String password) {
        User user = this.userService.getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getAccount, account));

        // 密码加密
        String passwordMd5 = DigestUtils.md5Hex(password);

        if (user == null || !user.getPassword().equals(passwordMd5)) {
            return R.fail(ExceptionCode.JWT_USER_INVALID);
        }

        return R.success(user);
    }
}
