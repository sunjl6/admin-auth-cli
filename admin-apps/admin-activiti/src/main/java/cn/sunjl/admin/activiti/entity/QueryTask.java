package cn.sunjl.admin.activiti.entity;

import cn.sunjl.admin.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.activiti.engine.task.DelegationState;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
//@TableName("")
@ApiModel(value = "Process Task", description = "流程task")
public class QueryTask extends Entity<Long> {

    @ApiModelProperty(value = "节点任务id")
    private String taskId;
    @ApiModelProperty(value = "节点任务名称")
    private String name;
    @ApiModelProperty(value = "流程的名称")
    private String processInstatnceName;
    @ApiModelProperty(value = "节点任务描述")
    private String description;
    @ApiModelProperty(value = "节点任务优先权")
    private int priority;
    @ApiModelProperty(value = "节点任务owner")
    private String owner;
    @ApiModelProperty(value = "节点任务执行者")
    private String assignee;
    @ApiModelProperty(value = "节点任务的流程id")
    private String processInstanceId;
    @ApiModelProperty(value = "节点任务executionId")
    private String executionId;
    @ApiModelProperty(value = "节点任务流程定义的id")
    private String processDefinitionId;
    @ApiModelProperty(value = "节点任务创建时间")
    private Date TaskCreateTime;
    @ApiModelProperty(value = "节点任务key")
    private String taskDefinitionKey;
    @ApiModelProperty(value = "节点任务的due时间")
    private Date dueDate;
    @ApiModelProperty(value = "节点任务目录")
    private String category;
    @ApiModelProperty(value = "节点任务名称")
    private String parentTaskId;
    @ApiModelProperty(value = "节点任务父任务taskid")
    private String tenantId;
    @ApiModelProperty(value = "节点任务tenantId")
    private String formKey;
    @ApiModelProperty(value = "节点任务局部变量")
    private Map<String, Object> taskLocalVariables;
    @ApiModelProperty(value = "节点任务全局变量")
    private Map<String, Object> processVariables;
    @ApiModelProperty(value = "节点任务拾取时间")
    private Date claimTime;
    @ApiModelProperty(value = "delegationState")
    private DelegationState delegationState;
    @ApiModelProperty(value = "是否挂起")
    private Boolean isSuspended;

}
