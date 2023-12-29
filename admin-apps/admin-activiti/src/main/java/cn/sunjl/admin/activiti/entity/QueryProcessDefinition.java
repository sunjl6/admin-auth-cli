package cn.sunjl.admin.activiti.entity;

import cn.sunjl.admin.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
//@TableName("")
@ApiModel(value = "Process Definition", description = "部署的流程(详细)")
public class QueryProcessDefinition extends Entity<Long> {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ACT_RE_PROCDEF的ID")
    private String ID;
    @ApiModelProperty(value = "目录")
    private String category;
    @ApiModelProperty(value = "名称(画BPMN图定义的名称)")
    private String name;
    @ApiModelProperty(value = "key画BPMN时候输入的key")
    private String key;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "版本")
    private int version;
    @ApiModelProperty(value = "就是BPMN上传的文件名")
    private String resourceName;
    @ApiModelProperty(value = "流程部署的另外张表的id")
    private String deploymentId;

    private String diagramResourceName;

    private boolean hasStartFormKey;

    private boolean hasGraphicalNotation;
    @ApiModelProperty(value = "是否挂起")
    private boolean isSuspended;

    private String TenantId;

    private String EngineVersion;
}
