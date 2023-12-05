package cn.sunjl.admin.authority.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class UserExt extends User{
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "部门名称")
    private String orgName;
    @ApiModelProperty(value = "岗位名称")
    private String stationName;
}
