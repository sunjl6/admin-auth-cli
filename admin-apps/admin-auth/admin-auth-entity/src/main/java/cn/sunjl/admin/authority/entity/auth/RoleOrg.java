package cn.sunjl.admin.authority.entity.auth;

import java.time.LocalDateTime;

import cn.sunjl.admin.base.entity.SuperEntity;
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

/**
 * <p>
 * 实体类
 * 角色组织关系
 * </p>
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("admin_auth_role_org")
@ApiModel(value = "RoleOrg", description = "角色组织关系")
public class RoleOrg extends SuperEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     * #admin_auth_role
     */
    @ApiModelProperty(value = "角色ID")
    @TableField("role_id")
    private Long roleId;

    /**
     * 部门ID
     * #admin_core_org
     */
    @ApiModelProperty(value = "部门ID")
    @TableField("org_id")
    private Long orgId;


    @Builder
    public RoleOrg(Long id, LocalDateTime createTime, Long createUser,
                   Long roleId, Long orgId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.roleId = roleId;
        this.orgId = orgId;
    }

}
