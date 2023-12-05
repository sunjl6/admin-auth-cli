package cn.sunjl.admin.authority.controller.auth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.sunjl.admin.authority.biz.service.auth.RoleAuthorityService;
import cn.sunjl.admin.authority.biz.service.auth.RoleOrgService;
import cn.sunjl.admin.authority.biz.service.auth.RoleService;
import cn.sunjl.admin.authority.biz.service.auth.UserRoleService;
import cn.sunjl.admin.authority.dto.auth.*;
import cn.sunjl.admin.authority.entity.auth.Role;
import cn.sunjl.admin.authority.entity.auth.RoleAuthority;
import cn.sunjl.admin.authority.entity.auth.UserRole;
import cn.sunjl.admin.authority.enumeration.auth.AuthorizeType;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.base.entity.SuperEntity;
import cn.sunjl.admin.database.mybatis.conditions.Wraps;
import cn.sunjl.admin.database.mybatis.conditions.query.LbqWrapper;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.log.annotation.SysLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 前端控制器
 * 角色
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/role")
@Api(value = "Role", tags = "角色")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleAuthorityService roleAuthorityService;
    @Autowired
    private RoleOrgService roleOrgService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private DozerUtils dozer;
    /**
     * 分页查询角色
     */
    @ApiOperation(value = "分页查询角色", notes = "分页查询角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    @SysLog("分页查询角色")
    public R<IPage<Role>> page(RolePageDTO param) {
        IPage<Role> page = getPage();
        Role role = dozer.map(param, Role.class);
        // 构建值不为null的查询条件
        LbqWrapper<Role> query = Wraps.lbQ(role)
                .geHeader(Role::getCreateTime, param.getStartCreateTime())
                .leFooter(Role::getCreateTime, param.getEndCreateTime())
                .orderByDesc(Role::getId);
        roleService.page(page, query);
        return success(page);
    }

    /**
     * 查询角色
     */
    @ApiOperation(value = "查询角色", notes = "查询角色")
    @GetMapping("/{id}")
    @SysLog("查询角色")
    public R<RoleQueryDTO> get(@PathVariable Long id) {
        Role role = roleService.getById(id);

        RoleQueryDTO roleQueryDTO = dozer.map(role, RoleQueryDTO.class);
        List<Long> orgList = roleOrgService.listOrgByRoleId(role.getId());
        roleQueryDTO.setOrgList(orgList);

        return success(roleQueryDTO);
    }

    @ApiOperation(value = "检测角色编码", notes = "检测角色编码")
    @GetMapping("/check/{code}")
    @SysLog("新增角色")
    public R<Boolean> check(@PathVariable String code) {
        return success(roleService.check(code));
    }

    /**
     * 新增角色
     */
    @ApiOperation(value = "新增角色", notes = "新增角色不为空的字段")
    @PostMapping
    @SysLog("新增角色")
    public R<RoleSaveDTO> save(@RequestBody @Validated RoleSaveDTO data) {
        roleService.saveRole(data, getUserId());
        return success(data);
    }

    /**
     * 修改角色
     */
    @ApiOperation(value = "修改角色", notes = "修改角色不为空的字段")
    @PutMapping
    @SysLog("修改角色")
    public R<RoleUpdateDTO> update(@RequestBody @Validated(SuperEntity.Update.class) RoleUpdateDTO data) {
        roleService.updateRole(data, getUserId());
        return success(data);
    }

    /**
     * 删除角色
     */
    @ApiOperation(value = "删除角色", notes = "根据id物理删除角色")
    @DeleteMapping
    @SysLog("删除角色")
    public R<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {
        roleService.removeById(ids);
        return success(true);
    }

    /**
     * 给用户分配角色
     */
    @ApiOperation(value = "给用户分配角色", notes = "给用户分配角色")
    @PostMapping("/user")
    @SysLog("给角色分配用户")
    public R<Boolean> saveUserRole(@RequestBody UserRoleSaveDTO userRole) {
        return success(roleAuthorityService.saveUserRole(userRole));
    }

    /**
     * 查询角色的用户
     */
    @ApiOperation(value = "查询角色的用户", notes = "查询角色的用户")
    @GetMapping("/user/{roleId}")
    @SysLog("查询角色的用户")
    public R<List<Long>> findUserIdByRoleId(@PathVariable Long roleId) {
        List<UserRole> list = userRoleService.list(Wraps.<UserRole>lbQ().eq(UserRole::getRoleId, roleId));
        return success(list.stream().mapToLong(UserRole::getUserId).boxed().collect(Collectors.toList()));
    }

    /**
     * 查询角色拥有的资源id
     */
    @ApiOperation(value = "查询角色拥有的资源id集合", notes = "查询角色拥有的资源id集合")
    @GetMapping("/authority/{roleId}")
    @SysLog("查询角色拥有的资源id集合")
    public R<RoleAuthoritySaveDTO> findAuthorityIdByRoleId(@PathVariable Long roleId) {
        List<RoleAuthority> list = roleAuthorityService.list(Wraps.<RoleAuthority>lbQ().eq(RoleAuthority::getRoleId, roleId));
        List<Long> menuIdList = list.stream().filter(item -> AuthorizeType.MENU.eq(item.getAuthorityType())).mapToLong(RoleAuthority::getAuthorityId).boxed().collect(Collectors.toList());
        List<Long> resourceIdList = list.stream().filter(item -> AuthorizeType.RESOURCE.eq(item.getAuthorityType())).mapToLong(RoleAuthority::getAuthorityId).boxed().collect(Collectors.toList());
        RoleAuthoritySaveDTO roleAuthority = RoleAuthoritySaveDTO.builder()
                .menuIdList(menuIdList).resourceIdList(resourceIdList)
                .build();
        return success(roleAuthority);
    }


    /**
     * 给角色配置权限
     */
    @ApiOperation(value = "给角色配置权限", notes = "给角色配置权限")
    @PostMapping("/authority")
    @SysLog("给角色配置权限")
    public R<Boolean> saveRoleAuthority(@RequestBody RoleAuthoritySaveDTO roleAuthoritySaveDTO) {
        return success(roleAuthorityService.saveRoleAuthority(roleAuthoritySaveDTO));
    }


    /**
     * 根据角色编码查询用户ID
     */
    @ApiOperation(value = "根据角色编码查询用户ID", notes = "根据角色编码查询用户ID")
    @GetMapping("/codes")
    @SysLog("根据角色编码查询用户ID")
    public R<List<Long>> findUserIdByCode(@RequestParam(value = "codes") String[] codes) {
        return success(roleService.findUserIdByCode(codes));
    }

    // 获取所有角色 不分页
    @ApiOperation(value = "获取所有角色", notes = "获取所有角色")
    @GetMapping("/list")
    @SysLog("获取所有角色")
    public R<List<Role>> list() {
        return success(roleService.list());
    }

    // 给用户分配多个角色 使用 @RequestBody UserRoleSaveDTO userRole
    @ApiOperation(value = "更新用户角色", notes = "更新用户角色")
    @PutMapping("/assignRoles/{userId}")
    @SysLog("更新用户角色")
    public R assignRoles(@PathVariable Long userId,@RequestParam("roleIds[]") List<Long> ids) {
        QueryWrapper<UserRole> wrapper = new QueryWrapper();
        wrapper.eq("user_id",userId);
        List<UserRole> listRole = userRoleService.list(wrapper);
        Long[] rolesIdsInSql = listRole.stream().map(UserRole::getRoleId).toArray(Long[]::new);


        List<Long> a = ids;
        System.out.println(a);
        List<Long> b = Arrays.asList(rolesIdsInSql);
        System.out.println(b);

        // 这个循环是 比较数据库拿出来的爵角色是否包含我这个 如果没用 那么就插入数据
        for (int i = 0; i < a.size(); i++) {
            Boolean isExist = isExist(a.get(i), b);
            if (isExist==false){
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(a.get(i));
                userRole.setCreateUser(this.getUserId());
                userRoleService.save(userRole);
            }
        }


        // 这个循环是反过来 把b'里的元素和a比较 如果没用就删除 ，有说明一样的 不做变动
        for (int i = 0; i < b.size(); i++) {
            Boolean isExist = isExist(b.get(i), a);
//            System.out.println("当前角色id"+b.get(i)+"和传递过来的id比较存在的值是"+isExist);
            if (isExist==false){
                // 这里判断没用了 那么就删除这条记录
                QueryWrapper<UserRole> wrapper1 = new QueryWrapper();
                wrapper1.eq("user_id",userId).eq("role_id",b.get(i));
                userRoleService.remove(wrapper1);
            }
        }

        return success("修改角色成功");
    }

    // 数组判断是否有一样的，元素
    private Boolean isExist (Long id,List<Long> list){
        boolean b = false;
        for (int i = 0; i < list.size(); i++) {
            Long sqlId = list.get(i);
            if (id.equals(sqlId)){
                b = true;
                break;
            }
        }
        return b;
    }
}