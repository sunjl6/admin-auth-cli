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

    @ApiModelProperty(value = "节点任务名称")
    private String taskId;
    @ApiModelProperty(value = "节点任务名称")
    private String name;
    @ApiModelProperty(value = "节点任务名称")
    private String description;
    @ApiModelProperty(value = "节点任务名称")
    private int priority;
    @ApiModelProperty(value = "节点任务名称")
    private String owner;
    @ApiModelProperty(value = "节点任务名称")
    private String assignee;
    @ApiModelProperty(value = "节点任务名称")
    private String processInstanceId;
    @ApiModelProperty(value = "节点任务名称")
    private String executionId;
    @ApiModelProperty(value = "节点任务名称")
    private String processDefinitionId;
    @ApiModelProperty(value = "节点任务名称")
    private Date TaskCreateTime;
    @ApiModelProperty(value = "节点任务名称")
    private String taskDefinitionKey;
    @ApiModelProperty(value = "节点任务名称")
    private Date dueDate;
    @ApiModelProperty(value = "节点任务名称")
    private String category;
    @ApiModelProperty(value = "节点任务名称")
    private String parentTaskId;
    @ApiModelProperty(value = "节点任务名称")
    private String tenantId;
    @ApiModelProperty(value = "节点任务名称")
    private String formKey;
    @ApiModelProperty(value = "节点任务名称")
    private Map<String, Object> taskLocalVariables;
    @ApiModelProperty(value = "节点任务名称")
    private Map<String, Object> processVariables;
    @ApiModelProperty(value = "节点任务名称")
    private Date claimTime;
    @ApiModelProperty(value = "delegationState")
    private DelegationState delegationState;
}
