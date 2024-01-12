package cn.sunjl.admin.activiti.entity;

import cn.sunjl.admin.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
//@TableName("admin_auth_resource")
@ApiModel(value = "Instance", description = "流程实例")
public class QueryProcessInstance extends Entity<Long> {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "id")
    private String processInstatnceId;
    @ApiModelProperty(value = "流程定义的id")
    private String processDefinitionId;
    @ApiModelProperty(value = "流程定义的名称")
    private String processDefinitionName;
    @ApiModelProperty(value = "流程定义的key")
    private String processDefinitionKey;
    @ApiModelProperty(value = "流程定义的版本")
    private Integer processDefinitionVersion;
    @ApiModelProperty(value = "流程部署的id")
    private String deploymentId;
    @ApiModelProperty(value = "业务id")
    private String businessKey;
    @ApiModelProperty(value = "是否挂起")
    private boolean isSuspended;
    @ApiModelProperty(value = "流程变量参数信息")
    private Map<String, Object> processVariables;
    @ApiModelProperty(value = "tenantid")
    private String tenantId;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "localizedName")
    private String localizedName;
    @ApiModelProperty(value = "localizedDescription")
    private String localizedDescription;
    @ApiModelProperty(value = "流程发起时间")
    private Date startTime;
    @ApiModelProperty(value = "流程发起用户id")
    private String startUserId;
    @ApiModelProperty(value = "activityId")
    private String activityId;
    @ApiModelProperty(value = "parentId")
    private String parentId;
    @ApiModelProperty(value = "arentProcessInstanceId")
    private String parentProcessInstanceId;
    @ApiModelProperty(value = "processInstanceId")
    private String processInstanceId;
    @ApiModelProperty(value = "rootProcessInstanceId")
    private String rootProcessInstanceId;
    @ApiModelProperty(value = "superExecutionId")
    private String superExecutionId;
    @ApiModelProperty(value = "是否开启")
    private boolean isEnded;
    @ApiModelProperty(value = "谁开启的流程")
    private String starInstatncetUser;
    //谁开启的流程
}
