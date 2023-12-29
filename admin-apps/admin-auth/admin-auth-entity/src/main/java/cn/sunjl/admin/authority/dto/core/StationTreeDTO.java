package cn.sunjl.admin.authority.dto.core;

import cn.sunjl.admin.authority.entity.core.Org;
import cn.sunjl.admin.authority.entity.core.Station;
import cn.sunjl.admin.model.ITreeNode;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@Data
@ApiModel(value = "StationTreeDTO", description = "岗位树")
public class StationTreeDTO extends Station implements ITreeNode<StationTreeDTO, Long> {

    private List<StationTreeDTO> children;

    private String label;

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public Long getCreateUser() {
        return super.getCreateUser();
    }

    @Override
    public Long getUpdateUser() {
        return super.getUpdateUser();
    }
    @Override
    public List<StationTreeDTO> getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(List<StationTreeDTO> children) {
        this.children = children;
    }

    public String getLabel() {
        return this.getName();
    }

}