package cn.sunjl.admin.activiti.dto;

import cn.sunjl.admin.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "bpm", description = "bpm内容")
public class Bpm implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "xmlStr")
    @NotNull(message = "不能为空", groups = SuperEntity.Update.class)
    private String xmlStr;
}
