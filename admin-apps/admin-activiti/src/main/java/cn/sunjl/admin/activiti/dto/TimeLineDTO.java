package cn.sunjl.admin.activiti.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TimeLineDTO", description = "流程时间线DTO")
public class TimeLineDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "节点名称")
    private String name;

    @ApiModelProperty(value = "开始时间")
    private LocalDate startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDate endTime;

    @ApiModelProperty(value = "受理人")
    private String assignee;
}
