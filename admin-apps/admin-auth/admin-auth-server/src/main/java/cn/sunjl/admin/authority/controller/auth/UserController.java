package cn.sunjl.admin.authority.controller.auth;
import cn.sunjl.admin.authority.biz.service.auth.RoleService;
import cn.sunjl.admin.authority.biz.service.auth.UserService;
import cn.sunjl.admin.authority.biz.service.core.OrgService;
import cn.sunjl.admin.authority.biz.service.core.StationService;
import cn.sunjl.admin.authority.dto.auth.*;
import cn.sunjl.admin.authority.entity.auth.Role;
import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.authority.entity.core.Org;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.base.entity.SuperEntity;
import cn.sunjl.admin.context.BaseContextConstants;
import cn.sunjl.admin.database.mybatis.conditions.Wraps;
import cn.sunjl.admin.database.mybatis.conditions.query.LbqWrapper;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.log.annotation.SysLog;
import cn.sunjl.admin.user.feign.UserQuery;
import cn.sunjl.admin.user.model.SysOrg;
import cn.sunjl.admin.user.model.SysRole;
import cn.sunjl.admin.user.model.SysStation;
import cn.sunjl.admin.user.model.SysUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.DCONST;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.sunjl.admin.base.R.success;
import static cn.sunjl.admin.context.BaseContextConstants.JWT_KEY_USER_ID;

/**
 * 前端控制器
 * 用户
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@Api(value = "User", tags = "用户")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private StationService stationService;
    @Autowired
    private DozerUtils dozer;

    /**
     * 分页查询用户
     */
    @ApiOperation(value = "分页查询用户", notes = "分页查询用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "分页条数", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    @SysLog("分页查询用户")
    public R<IPage<User>> page(UserPageDTO userPage) {
        IPage<User> page = getPage();

        User user = dozer.map2(userPage, User.class);
        if (userPage.getOrgId() != null && userPage.getOrgId() >= 0) {
            user.setOrgId(null);
        }
        LbqWrapper<User> wrapper = Wraps.lbQ(user);
        if (userPage.getOrgId() != null && userPage.getOrgId() >= 0) {
            List<Org> children = orgService.findChildren(Arrays.asList(userPage.getOrgId()));
            wrapper.in(User::getOrgId, children.stream().mapToLong(Org::getId).boxed().collect(Collectors.toList()));
        }
        wrapper.geHeader(User::getCreateTime, userPage.getStartCreateTime())
                .leFooter(User::getCreateTime, userPage.getEndCreateTime())
                .like(User::getName, userPage.getName())
                .like(User::getAccount, userPage.getAccount())
                .like(User::getEmail, userPage.getEmail())
                .like(User::getMobile, userPage.getMobile())
                .eq(User::getSex, userPage.getSex())
                .eq(User::getStatus, userPage.getStatus())
                .orderByDesc(User::getId);
//        userService.page(page, wrapper);

        userService.findPage(page, wrapper);
        return success(page);
    }

    /**
     * 查询用户
     */
    @ApiOperation(value = "查询用户", notes = "查询用户")
    @GetMapping("/{id}")
    @SysLog("查询用户")
    public R<User> get(@PathVariable Long id) {
        String token = request.getHeader(JWT_KEY_USER_ID);
        return success(userService.getById(id));
    }

// 查询所以用户
    @ApiOperation(value = "查询所有用户", notes = "查询所有用户")
    @GetMapping("/find")
    @SysLog("查询所有用户")
    public R<List<Long>> findAllUserId() {
        return success(userService.list().stream().mapToLong(User::getId).boxed().collect(Collectors.toList()));
    }

    /**
     * 新增用户
     */
    @ApiOperation(value = "新增用户", notes = "新增用户不为空的字段")
    @PostMapping
    @SysLog("新增用户")
    public R<User> save(@RequestBody @Validated UserSaveDTO data) {
        User user = dozer.map(data, User.class);
        userService.saveUser(user);
        return success(user);
    }

    /**
     * 修改用户
     */
    @ApiOperation(value = "修改用户", notes = "修改用户不为空的字段")
    @PutMapping
    @SysLog("修改用户")
    public R<User> update(@RequestBody @Validated(SuperEntity.Update.class) UserUpdateDTO data) {
        User user = dozer.map(data, User.class);
        userService.updateUser(user);
        return success(user);
    }

    @ApiOperation(value = "修改头像", notes = "修改头像")
    @PutMapping("/avatar")
    @SysLog("修改头像")
    public R<User> avatar(@RequestBody @Validated(SuperEntity.Update.class) UserUpdateAvatarDTO data) {
        User user = dozer.map(data, User.class);
        userService.updateUser(user);
        return success(user);
    }

    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PutMapping("/password")
    @SysLog("修改密码")
    public R<Boolean> updatePassword(@RequestBody UserUpdatePasswordDTO data) {
        return success(userService.updatePassword(data));
    }

    @ApiOperation(value = "重置密码", notes = "重置密码")
    @GetMapping("/reset")
    @SysLog("重置密码")
    public R<Boolean> resetTx(@RequestParam("ids[]") List<Long> ids) {
        userService.reset(ids);
        return success();
    }

    /**
     * 删除用户
     */
    @ApiOperation(value = "删除用户", notes = "根据id物理删除用户")
    @DeleteMapping
    @SysLog("删除用户")
    public R<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {
        userService.removeByIds(ids);
        return success(true);
    }


    /**
     * 单体查询用户
     */
    @ApiOperation(value = "查询用户详细", notes = "查询用户详细")
    @PostMapping(value = "/anno/id/{id}")
    public R<SysUser> getById(@PathVariable Long id, @RequestBody UserQuery query) {
        User user = userService.getById(id);
        if (user == null) {
            return success(null);
        }
        SysUser sysUser = dozer.map(user, SysUser.class);

        if (query.getFull() || query.getOrg()) {
            sysUser.setOrg(dozer.map(orgService.getById(user.getOrgId()), SysOrg.class));
        }
        if (query.getFull() || query.getStation()) {
            sysUser.setStation(dozer.map(stationService.getById(user.getStationId()), SysStation.class));
        }

        if (query.getFull() || query.getRoles()) {
            List<Role> list = roleService.findRoleByUserId(id);
            sysUser.setRoles(dozer.mapList(list, SysRole.class));
        }

        return success(sysUser);
    }
    //从token 获取id 查询
    @ApiOperation(value = "通过token查询用户详细", notes = "通过token查询用户详细")
    @GetMapping(value = "/profile")
    public R<SysUser> getUserInfoByToken() {
        UserQuery query = new UserQuery();
        query.setFull(true);
        query.setStation(true);
        query.setRoles(true);
        query.setOrg(true);
        Long id = Long.valueOf(request.getHeader(JWT_KEY_USER_ID));
        User user = userService.getById(id);
        if (user == null) {
            return success(null);
        }
        SysUser sysUser = dozer.map(user, SysUser.class);
        sysUser.setPhoto(user.getAvatar());

        if (query.getFull() || query.getOrg()) {
            sysUser.setOrg(dozer.map(orgService.getById(user.getOrgId()), SysOrg.class));
        }
        if (query.getFull() || query.getStation()) {
            sysUser.setStation(dozer.map(stationService.getById(user.getStationId()), SysStation.class));
        }

        if (query.getFull() || query.getRoles()) {
            List<Role> list = roleService.findRoleByUserId(id);
            sysUser.setRoles(dozer.mapList(list, SysRole.class));
        }

        return success(sysUser);
    }

    /**
     * 查询角色的已关联用户
     * @param roleId  角色id
     * @param keyword 账号account或名称name
     */
    @ApiOperation(value = "查询角色的已关联用户", notes = "查询角色的已关联用户")
    @GetMapping(value = "/role/{roleId}")
    public R<UserRoleDTO> findUserByRoleId(@PathVariable("roleId") Long roleId, @RequestParam(value = "keyword", required = false) String keyword) {
        List<User> list = userService.findUserByRoleId(roleId, keyword);
        List<Long> idList = list.stream().mapToLong(User::getId).boxed().collect(Collectors.toList());
        return success(UserRoleDTO.builder().idList(idList).userList(list).build());
    }
}