package cn.sunjl.admin.activiti.entity;

import cn.sunjl.admin.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
//@TableName("admin_auth_resource")
@ApiModel(value = "Deployment", description = "部署的流程(简)")
public class QueryDeployment extends Entity<Long> {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ACT_RE_DEPLOYMENT流程id")
    private String deployid;
    @ApiModelProperty(value = "ACT_RE_DEPLOYMENT流程名字 部署流程的时候传进去的")
    private String name;
    @ApiModelProperty(value = "创建时间")
    private Date deploymentTime;
    @ApiModelProperty(value = "流程部署目录")
    private String category;
    @ApiModelProperty(value = "流程不是的key")
    private String key;

    private String tenantId;
    @ApiModelProperty(value = "流程部署的版本")
    private Integer version;
    @ApiModelProperty(value = "释放的版本")
    private String projectReleaseVersion;
}
