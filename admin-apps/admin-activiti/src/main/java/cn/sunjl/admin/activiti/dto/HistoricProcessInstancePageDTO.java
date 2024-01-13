package cn.sunjl.admin.activiti.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class HistoricProcessInstancePageDTO {


    /**
     * id
     */
//    @ApiModelProperty(value = "id")
//    private String id;

    /**
     * processInstanceId
     */
    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

    /**
     * processDefinitionId
     */
    @ApiModelProperty(value = "流程定义id")
    private String processDefinitionId;


    /**
     * startTime
     */
//    @ApiModelProperty(value = "开始时间")
//    private LocalDate startTime;

    /**
     * endTime
     */
//    @ApiModelProperty(value = "结束时间")
//    private LocalDate endTime;

    /**
     * durationInMillis
     */
//    @ApiModelProperty(value = "持续时间")
//    private Long durationInMillis;

    /**
     * endActivityId
     */
//    @ApiModelProperty(value = "结束节点id")
//    private String endActivityId;
    /**
     * startUserId
     */
    @ApiModelProperty(value = "流程启动者账号")
    private String startUserId;

    /**
     * startActivityId
     */
//    @ApiModelProperty(value = "开始节点id")
//    private String startActivityId;

    /**
     * tenantId
     */
//    @ApiModelProperty(value = "tennatId")
//    private String tenantId;

    /**
     * processDefinitionKey
     */
//    @ApiModelProperty(value = "流程定义key")
//    private String processDefinitionKey;

    /**
     * processDefinitionName
     */
    @ApiModelProperty(value = "流程名称")
    private String processDefinitionName;

    /**
     * processDefinitionVersion
     */
//    @ApiModelProperty(value = "流程定义版本")
//    private String processDefinitionVersion;

    /**
     * deploymentId
     */
//    @ApiModelProperty(value = "deploymentId")
//    private String deploymentId;


    /**
     * persistentState
     */
//    @ApiModelProperty(value = "persistentState")
//    private Map<String,Object> persistentState;


    /**
     * processVariables
     */
//    @ApiModelProperty(value = "流程变量")
//    private Map<String, Object> processVariables;


    /**
     * inserted
     */
//    @ApiModelProperty(value = "插入")
//    private String inserted;

    /**
     * updated
     */
//    @ApiModelProperty(value = "更新")
//    private String updated;

    /**
     * deleted
     */
//    @ApiModelProperty(value = "删除")
//    private String deleted;



}
