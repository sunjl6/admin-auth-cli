package cn.sunjl.admin.authority.biz.service.auth.impl;

import cn.sunjl.admin.authority.biz.service.auth.ResourceService;
import cn.sunjl.admin.authority.biz.service.auth.UserService;
import cn.sunjl.admin.authority.dto.auth.LoginDTO;
import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.dto.auth.UserDTO;
import cn.sunjl.admin.authority.entity.auth.Resource;
import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.exception.BizException;
import cn.sunjl.admin.exception.code.ExceptionCode;
import cn.sunjl.admin.jwt.server.utils.JwtTokenServerUtils;
import cn.sunjl.admin.jwt.utils.JwtUserInfo;
import cn.sunjl.admin.jwt.utils.Token;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        Boolean accountStatus = checkStatus(account);
        if (result.getIsError()) {
            return R.fail(result.getCode(), result.getMsg());
        }
        // 判断账号是否停用
        if (accountStatus == false){
            return R.fail("账号已停用,请联系领导");
        }
        User user = result.getData();

        // 生成jwt token
        Token token = this.generateUserToken(user);

        List<Resource> resourceList =this.resourceService.
                findVisibleResource(ResourceQueryDTO.builder().
                        userId(user.getId()).build());
        List<String> permissionsList = null;
        System.out.println(resourceList);
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

    // 验证账号是否停用
    private Boolean checkStatus(String account){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        User user = this.userService.getOne(wrapper);
        if (user == null){
            throw new BizException("账号或者密码错误");
        }
        return user.getStatus();
    }
    // 登录验证
    private R<User> checkUser(String account, String password) {
        User user = this.userService.getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getAccount, account));

        // 密码加密
        String passwordMd5 = DigestUtils.md5Hex(password);
//        System.out.println("md5加密后的密码"+passwordMd5);
//        System.out.println("数据库获取的密码" + user.getPassword());
//        System.out.println("用户是"+user.getAccount());
        if (user == null || !user.getPassword().equals(passwordMd5)) {
            return R.fail(ExceptionCode.JWT_USER_INVALID);
        }

        return R.success(user);
    }
}
