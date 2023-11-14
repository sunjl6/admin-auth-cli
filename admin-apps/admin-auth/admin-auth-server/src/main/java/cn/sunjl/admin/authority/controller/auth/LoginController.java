package cn.sunjl.admin.authority.controller.auth;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.sunjl.admin.authority.biz.service.auth.UserService;
import cn.sunjl.admin.authority.biz.service.auth.ValidateCodeService;
import cn.sunjl.admin.authority.biz.service.auth.impl.AuthManager;
import cn.sunjl.admin.authority.biz.service.common.LoginLogService;
import cn.sunjl.admin.authority.dto.auth.LoginDTO;
import cn.sunjl.admin.authority.dto.auth.LoginParamDTO;
import cn.sunjl.admin.authority.entity.common.LoginLog;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.log.annotation.SysLog;
import cn.sunjl.admin.log.util.AddressUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/anno")
@Api(tags = "用户登入模块",value = "UserLoginController")
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private AuthManager authManager;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginLogService loginLogService;

    //生成验证码并且response的流写返回
    @SysLog("生成验证码操作记录")
    @RequestMapping(value = "/captcha",method = RequestMethod.GET,produces = "image/png")
    @ApiOperation(value = "验证码",notes = "生成验证码")
    public void captcha(@RequestParam(value = "key") String key, HttpServletResponse response) throws IOException {
        validateCodeService.create(key,response);
    }
    //校验验证码返回TrueOrFalse
    @RequestMapping(value = "/checkCode",method = RequestMethod.POST)
    @ApiOperation(value = "校验验证码",notes = "校验验证码")
    public Boolean check(@RequestBody LoginParamDTO loginParamDTO){

        return validateCodeService.checkCode(loginParamDTO.getKey(),loginParamDTO.getCode());
    }

    //用户登入
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ApiOperation(value = "登入",notes = "登入")
    @SysLog("用户登入记录日志")
    public R<LoginDTO> login(@Validated @RequestBody LoginParamDTO loginParamDTO){
        boolean checkCode = validateCodeService.checkCode(loginParamDTO.getKey(), loginParamDTO.getCode());
        if (checkCode){
        // 验证码成功，调用登入模块
            R<LoginDTO> r = authManager.login(loginParamDTO.getAccount(),loginParamDTO.getPassword());

            // 写入登入日志
            String ua = StrUtil.sub(this.request.getHeader("user-agent"), 0, 500);
            String ip = ServletUtil.getClientIP(this.request);
            String location = AddressUtil.getRegion(ip);
            String account = loginParamDTO.getAccount();
            this.userService.updateLoginTime(account);
            loginLogService.save(account, ua, ip, location, "");
            return r;

        }
        //验证码失败，直接返回错
        return this.success(null);
    }
}
