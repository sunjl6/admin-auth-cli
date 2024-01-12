package cn.sunjl.admin.activiti.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("ACT_RU_VARIABLE")
@ApiModel(value = "ACT_RU_VARIABLE", description = "存放当前运行实例的变量")
public class ActRuVariable implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId("ID_")
    @TableField(value = "ID_", condition = LIKE)
    private String id;

    @ApiModelProperty(value = "rev")
    @TableField(value = "REV_", condition = LIKE)
    private int rev;

    @ApiModelProperty(value = "type")
    @TableField(value = "TYPE_", condition = LIKE)
    private String type;

    @ApiModelProperty(value = "name")
    @TableField(value = "NAME_", condition = LIKE)
    private String name;

    @ApiModelProperty(value = "executionId")
    @TableField(value = "EXECUTION_ID_", condition = LIKE)
    private String executionId;

    @ApiModelProperty(value = "procInstId")
    @TableField(value = "PROC_INST_ID_", condition = LIKE)
    private String procInstId;

    @ApiModelProperty(value = "taskId")
    @TableField(value = "TASK_ID_", condition = LIKE)
    private String taskId;

    @ApiModelProperty(value = "byteArrayId")
    @TableField(value = "BYTEARRAY_ID_", condition = LIKE)
    private String byteArrayId;

    @ApiModelProperty(value = "DOUBLE")
    @TableField(value = "DOUBLE_", condition = LIKE)
    private String double_;

    @ApiModelProperty(value = "LONG")
    @TableField(value = "LONG_", condition = LIKE)
    private String long_;

    @ApiModelProperty(value = "text")
    @TableField(value = "TEXT_", condition = LIKE)
    private String text;

    @ApiModelProperty(value = "text2")
    @TableField(value = "TEXT2_", condition = LIKE)
    private String text2;
}
