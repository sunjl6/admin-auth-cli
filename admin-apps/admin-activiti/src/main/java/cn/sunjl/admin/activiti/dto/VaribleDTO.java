package cn.sunjl.admin.activiti.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "VaribleDTO", description = "流程参数DTO")
public class VaribleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * key
     */
    @ApiModelProperty(value = "名称")
    @Length(max = 255, message = "名称长度不能超过255")
    private String key;
    /**
     * value
     *
     */
    @ApiModelProperty(value = "组织ID")
    private Object value;


}
