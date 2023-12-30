package cn.sunjl.admin.authority.controller.auth;
import cn.sunjl.admin.authority.biz.service.auth.RoleService;
import cn.sunjl.admin.authority.biz.service.auth.UserService;
import cn.sunjl.admin.authority.biz.service.core.OrgService;
import cn.sunjl.admin.authority.biz.service.core.StationService;
import cn.sunjl.admin.authority.config.MinioConfig;
import cn.sunjl.admin.authority.dto.auth.*;
import cn.sunjl.admin.authority.entity.auth.Role;
import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.authority.entity.auth.UserExt;
import cn.sunjl.admin.authority.entity.core.Org;
import cn.sunjl.admin.authority.utils.MinioUtil;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.base.entity.SuperEntity;
import cn.sunjl.admin.context.BaseContextConstants;
import cn.sunjl.admin.database.mybatis.conditions.Wraps;
import cn.sunjl.admin.database.mybatis.conditions.query.LbqWrapper;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.exception.BizException;
import cn.sunjl.admin.log.annotation.SysLog;
import cn.sunjl.admin.user.feign.UserQuery;
import cn.sunjl.admin.user.model.SysOrg;
import cn.sunjl.admin.user.model.SysRole;
import cn.sunjl.admin.user.model.SysStation;
import cn.sunjl.admin.user.model.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.DCONST;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
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
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private MinioConfig minioConfig;

    // 用户修改头像
    @ApiOperation(value = "用户上传头像")
    @PostMapping("/uploadAvatar/{id}")
    public R upload(@RequestParam("file") MultipartFile file,@PathVariable Long id) {
        String url = "";
        User user = new User();
        String objectName = minioUtil.upload(file);
        if (null != objectName) {
            url = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + objectName;
            user.setId(id);
            user.setAvatar(url);
            userService.updateUser(user);
            return R.success(url);
        }
        return R.fail("上传失败");
    }


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
                .eq(User::getEmployeeId,userPage.getEmployeeId())
                .eq(User::getStatus, userPage.getStatus())
                .eq(User::getDeleted, 0)
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
    public R<UserExt> get(@PathVariable Long id) {
        String token = request.getHeader(JWT_KEY_USER_ID);
        User user = userService.getById(id);
        if (user.getOrgId() == null){
            return R.fail("获取不到部门信息，请先添加");
        }
        if (user.getStationId() == null) {
            return R.fail("无法获取岗位信息，请先添加");
        }
        String orgName = orgService.getById(user.getOrgId()).getName();
        String stationName = stationService.getById(user.getStationId()).getName();
        UserExt userExt = dozer.map(user, UserExt.class);
        userExt.setOrgName(orgName);
        userExt.setStationName(stationName);
        return success(userExt);
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
    // 批量新增用户
    @ApiOperation(value = "批量新增用户", notes = "批量新增用户用于excel导入")
    @PostMapping ("/saveUserBatch")
    @SysLog("新增用户")
    public R saveBatch(@RequestBody @Valid List<UserSaveDTO> dataList) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            System.out.println(dataList.get(i).getPassword());
            User user = dozer.map(dataList.get(i), User.class);
            user.setPassword(DigestUtils.md5Hex(dataList.get(i).getPassword()));
            userList.add(user);
        }
        return success(userService.saveBatch(userList));
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
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status",0);
        userService.removeByIds(ids);
        return success(true);
    }


    /**
     * 单体查询用户
     */
    @ApiOperation(value = "查询用户详细", notes = "查询用户详细")
    @PostMapping(value = "/anno/id/{id}")
    @SysLog("查询用户详细")
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
    @SysLog("通过token查询用户详细")
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
    @SysLog("查询角色的已关联用户")
    public R<UserRoleDTO> findUserByRoleId(@PathVariable("roleId") Long roleId, @RequestParam(value = "keyword", required = false) String keyword) {
        List<User> list = userService.findUserByRoleId(roleId, keyword);
        List<Long> idList = list.stream().mapToLong(User::getId).boxed().collect(Collectors.toList());
        return success(UserRoleDTO.builder().idList(idList).userList(list).build());
    }

    // 开启和禁用账号
    @ApiOperation(value = "开启和禁用账号", notes = "开启和禁用账号")
    @PutMapping(value = "/switchAccountStatus")
    @SysLog("开启和禁用账号")
    public R<User> switchAccountStatus(@RequestBody @Validated(SuperEntity.Update.class) UserUpdateDTO data) {
        User user = dozer.map(data, User.class);
        userService.updateUser(user);
        return success(user);
    }

}