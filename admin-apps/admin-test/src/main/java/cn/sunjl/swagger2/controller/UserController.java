package cn.sunjl.swagger2.controller;

import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.dozer.dto.UserDTO;
import cn.sunjl.dozer.entity.UserEntity;
import cn.sunjl.swagger2.entity.User;
import cn.sunjl.validator.entity.UserValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户控制器")
@Validated //开启校验功能
public class UserController {
    @Autowired
    private DozerUtils dozerUtils;

    @GetMapping("/getUsers")
    @ApiOperation(value = "查询所有用户", notes = "查询所有用户信息")
    public List<User> getAllUsers(){
        User user = new User();
        user.setId(100);
        user.setName("itcast");
        user.setAge(20);
        user.setAddress("bj");
        List<User> list = new ArrayList<>();
        list.add(user);
        return list;
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增用户", notes = "新增用户信息")
    public String save(@RequestBody User user){
        return "OK";
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改用户", notes = "修改用户信息")
    public String update(@RequestBody User user){
        return "OK";
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除用户", notes = "删除用户信息")
    public String delete(int id){
        return "OK";
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码",
                    required = true, type = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数",
                    required = true, type = "Integer"),
    })
    @ApiOperation(value = "分页查询用户信息")
    @GetMapping(value = "page/{pageNum}/{pageSize}")
    public String findByPage(@PathVariable Integer pageNum,
                             @PathVariable Integer pageSize) {
        return "OK";
    }


    // 测试对象装好
    @GetMapping("/mapper")
    public UserEntity mapper(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(10);
        userDTO.setName("sunjl");
        userDTO.setAge(20);
        userDTO.setAddress("上海");

        UserEntity userEntity = dozerUtils.map(userDTO, UserEntity.class);
        return userEntity;
    }
    // 测试校验模块
    //简单数据类型校验
    @RequestMapping("/delete2")
    public String delete2(@NotBlank(message = "id不能为空") String id){
        System.out.println("delete..." + id);
        return "OK";
    }

    //对象属性校验
    @RequestMapping("/save2")
    public String save2(@Validated UserValidator user){
        System.out.println("save..." + user);
        return "OK";
    }
}