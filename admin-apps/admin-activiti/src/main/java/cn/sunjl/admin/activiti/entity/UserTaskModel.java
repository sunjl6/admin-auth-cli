package cn.sunjl.admin.activiti.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserTaskModel implements Serializable {
    private String id;
    private String name;
    /**
     * 审批人类型
     * 0：未指定
     * 1：办理人
     * 2：候选用户
     * 3：候选组
     */
    private String type;
    /**
     * 办理人，办理人只能指定一个人，不能使用逗号分隔。
     * 默认执行签收操作taskService.claim(taskId, currentUserId);
     * 在ACT_HI_TASKINST和ACT_RU_TASK会产生数据，这两个表里面的Assignee_字段就是设置的办理人姓名或者对象的ID
     */
    private String assignee;
    /**
     * 候选用户，候选用户设置办理人不是很多的情况下使用，而且需要签收，
     * 也就是说我们常说的抢件模式,设置候选组的前提是没有指定Assignee，（即没有执行签收操作）。
     * 设置候选用户需要主动签收taskService.claim(taskId, currentUserId);
     */
//    private String candidateUsers;
     private List<String> candidateUsers;
    /**
     * 候选组，这个就是这只办理角色或者办理岗位，适合使用在办理人比较多的情况下，而且涉及到几个部门的情形。
     * 候选组与候选用户类似，只是要获取候选用户，需要根据候选组找到对应的用户
     * 设置候选组需要主动签收taskService.claim(taskId, currentUserId);
     */
    private String candidateGroups;
}
