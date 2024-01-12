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
@TableName("ACT_GE_BYTEARRAY")
@ApiModel(value = "ACT_GE_BYTEARRAY", description = "存放bpmn字节的表")
public class ActGeByteArrary implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId("ID_")
    @TableField(value = "ID_", condition = LIKE)
    private String id;

    @ApiModelProperty(value = "rev")
    @TableField(value = "REV_", condition = LIKE)
    private int rev;

    @ApiModelProperty(value = "name")
    @TableField(value = "NAME_", condition = LIKE)
    private String name;

    @ApiModelProperty(value = "deploymentId")
    @TableField(value = "DEPLOYMENT_ID_", condition = LIKE)
    private String deploymentId;

    @ApiModelProperty(value = "bytes")
    @TableField(value = "BYTES_", condition = LIKE)
    private byte[] bytes;

    @ApiModelProperty(value = "generated")
    @TableField(value = "GENERATED_", condition = LIKE)
    private int generated;
}
