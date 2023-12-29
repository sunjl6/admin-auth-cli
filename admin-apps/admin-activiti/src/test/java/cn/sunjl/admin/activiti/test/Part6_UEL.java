package cn.sunjl.admin.activiti.test;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
public class Part6_UEL {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    //启动流程实例带参数，执行执行人
    @Test
    public void initProcessInstanceWithArgs() {
        Map<String,Object> map = new HashMap<>(); // 这个map 用于存放指定的UEL表达式里的用户
        map.put("zhidingren","bajie");// 制动为 bajie
        // 创建实例 把用户八戒一起传递进去 那么这个实例 在执行人是 zhidingren 的地方就会被替换成 bajie
        runtimeService.startProcessInstanceById("part6_uel_v1:1:861f9909-9d56-11ee-8240-f426796d2a71",
                "2222222",
                map);
    }
    //完成任务带参数，指定流程变量测试
    @Test
    public void completeTaskWithArgs() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("pay", "101");
        taskService.complete("822447a5-9a2f-11ee-b336-f426796d2a71",variables);
        System.out.println("完成任务");
    }

    //启动流程实例带参数，使用实体类
    @Test
    public void initProcessInstanceWithClassArgs() {
        UEL_POJO uel_pojo = new UEL_POJO();
        uel_pojo.setZhixingren("bajie");
        //流程变量
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("uelpojo", uel_pojo);
        runtimeService.startProcessInstanceById("part6_uel_v3:2:d1f94df1-9a33-11ee-ab48-f426796d2a71",
                "classid",
                variables);
    }
    //任务完成环节带参数，指定多个候选人
    @Test
    public void initProcessInstanceWithCandiDateArgs() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("houxuanren", "wukong,tangseng");
        taskService.complete("3cea78ae-9a34-11ee-87e2-f426796d2a71",variables);
        System.out.println("完成任务");
    }

    //直接指定流程变量
    @Test
    public void otherArgs() {
        runtimeService.setVariable("4f6c9e23-d3ae-11ea-82ba-dcfb4875e032","pay","101");
//        runtimeService.setVariables();
//        taskService.setVariable();
//        taskService.setVariables();

    }

    //局部变量
    @Test
    public void otherLocalArgs() {
        runtimeService.setVariableLocal("4f6c9e23-d3ae-11ea-82ba-dcfb4875e032","pay","101");
//        runtimeService.setVariablesLocal();
//        taskService.setVariableLocal();
//        taskService.setVariablesLocal();
    }
}
