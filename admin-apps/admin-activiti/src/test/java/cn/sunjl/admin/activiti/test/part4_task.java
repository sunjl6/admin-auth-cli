package cn.sunjl.admin.activiti.test;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class part4_task {
    @Autowired
    private TaskService taskService;

    //查询当前用户的task
    @Test
    public void getTaskByUser(){
        List<Task> list = taskService.createTaskQuery().taskAssignee("bajie").list();
        for (Task tk : list){
            System.out.println("当前task Id："+tk.getId());
            System.out.println("当前任务名称："+tk.getName());
            System.out.println("谁来完成这个节点："+tk.getAssignee());
        }
    }

    // 完成当前task 任务需要传递 task id
    @Test
    public void finishTask(){
        taskService.complete("a9b33f3d-9a28-11ee-b75c-f426796d2a71");
    }

    // task 的拾取
    @Test
    public void claimTask(){
        Task tk = taskService.createTaskQuery().taskId("5aec2892-999f-11ee-885c-f426796d2a71").singleResult();
        System.out.println("当前task Id："+tk.getId());
        System.out.println("当前任务名称："+tk.getName());
        System.out.println("谁来完成这个节点："+tk.getAssignee());

        taskService.claim("5aec2892-999f-11ee-885c-f426796d2a71","bajie");
    }

    // task 的归还和指派新用户
    @Test
    public void claimTask2(){
        Task tk = taskService.createTaskQuery().taskId("5aec2892-999f-11ee-885c-f426796d2a71").singleResult();
        System.out.println("当前task Id："+tk.getId());
        System.out.println("当前任务名称："+tk.getName());
        System.out.println("谁来完成这个节点："+tk.getAssignee());
        taskService.claim("5aec2892-999f-11ee-885c-f426796d2a71",null);
    }


    // task 的归还和指派新用户
    @Test
    public void claimTask3(){
        Task tk = taskService.createTaskQuery().taskId("5aec2892-999f-11ee-885c-f426796d2a71").singleResult();
        System.out.println("当前task Id："+tk.getId());
        System.out.println("当前任务名称："+tk.getName());
        System.out.println("谁来完成这个节点："+tk.getAssignee());
        taskService.setAssignee("5aec2892-999f-11ee-885c-f426796d2a71","sjl");
    }
}
