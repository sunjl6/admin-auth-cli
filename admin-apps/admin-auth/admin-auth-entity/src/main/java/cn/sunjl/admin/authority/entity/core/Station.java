package cn.sunjl.admin.authority.entity.core;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

import cn.sunjl.admin.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 岗位
 * </p>
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("admin_core_station")
@ApiModel(value = "Station", description = "岗位")
public class Station extends Entity<Long> {
    private static final long serialVersionUID = 1L;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 255, message = "名称长度不能超过255")
    @TableField(value = "name", condition = LIKE)
    private String name;

    /**
     * 组织ID
     * #admin_core_org
     */
    @ApiModelProperty(value = "组织ID")
    @TableField("org_id")
    private Long orgId;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @TableField("status")
    private Boolean status;


    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    @TableField("parent_id")
    private Long parentId;

    /**
     * 树结构
     */
    @ApiModelProperty(value = "树结构")
    @Length(max = 255, message = "树结构长度不能超过255")
    @TableField(value = "tree_path", condition = LIKE)
    private String treePath;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    @TableField(value = "describe_", condition = LIKE)
    private String describe;

    // level 职位类型，1 普通员工 2，主管 3 经理 依次往上
    @ApiModelProperty(value = "职位等级(1:普通员工，2：主管，3：经理)")
    @TableField(value = "level", condition = LIKE)
    private String level;
    @Builder
    public Station(Long id,String level, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser,
                   String name, Long orgId, Boolean status, String describe, Long parentId, String treePath) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.name = name;
        this.orgId = orgId;
        this.status = status;
        this.describe = describe;
        this.parentId = parentId;
        this.treePath = treePath;
        this.level = level;
    }
}
