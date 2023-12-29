package cn.sunjl.admin.activiti.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("formdata")
@ApiModel(value = "formdata", description = "存放动态表单信息")
public class FormData implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "PROC_DEF_ID_")
    @TableField(value = "PROC_DEF_ID_", condition = LIKE)
    private String processDefinitionId;

    @ApiModelProperty(value = "PROC_INST_ID_")
    @TableField(value = "PROC_INST_ID_", condition = LIKE)
    private String processInstanceId;

    @ApiModelProperty(value = "FORM_KEY_")
    @TableField(value = "FORM_KEY_", condition = LIKE)
    private String formKey;

    @ApiModelProperty(value = "Control_ID_")
    @TableField(value = "Control_ID_", condition = LIKE)
    private String controlId;

    @ApiModelProperty(value = "Control_VALUE_")
    @TableField(value = "Control_VALUE_", condition = LIKE)
    private String controlValue;
}
