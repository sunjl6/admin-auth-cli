package cn.sunjl.admin.activiti.pojo;

import cn.sunjl.admin.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("ACT_RU_IDENTITYLINK")
@ApiModel(value = "IdentityLink", description = "task候选人，指向人信息")
public class IdentityLink implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId("ID_")
    @TableField(value = "ID_", condition = LIKE)
    private String id;


    @ApiModelProperty(value = "REV_")
    @TableField(value = "REV_", condition = LIKE)
    private String rev;


    @ApiModelProperty(value = "GROUP_ID_")
    @TableField(value = "GROUP_ID_", condition = LIKE)
    private String groupId;

    @ApiModelProperty(value = "TYPE_")
    @TableField(value = "TYPE_", condition = LIKE)
    private String type;


    @ApiModelProperty(value = "USER_ID_")
    @TableField(value = "USER_ID_", condition = LIKE)
    private String userId;


    @ApiModelProperty(value = "TASK_ID_")
    @TableField(value = "TASK_ID_", condition = LIKE)
    private String taskId;

    @ApiModelProperty(value = "PROC_INST_ID_")
    @TableField(value = "PROC_INST_ID_", condition = LIKE)
    private String procInstId;

    @ApiModelProperty(value = "PROC_DEF_ID_")
    @TableField(value = "PROC_DEF_ID_", condition = LIKE)
    private String procDefId;


}
