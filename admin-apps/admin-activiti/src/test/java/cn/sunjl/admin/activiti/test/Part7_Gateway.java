package cn.sunjl.admin.activiti.test;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class Part7_Gateway {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    //启动流程实例带参数，执行执行人

    // 并行网关
    @Test
    public void parallelGateway(){
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceById("part7_Parallel:1:0127dd24-9a43-11ee-a9e2-f426796d2a71");
        System.out.println("流程实例ID："+processInstance.getProcessDefinitionId());
    };
    @Test
    public void completeTask() {

        Map<String, Object> variables = new HashMap<String, Object>();

        //流程实例idecd41693-d3cd-11ea-ad34-dcfb4875e032

        taskService.complete("ae91fa13-9a44-11ee-b20d-f426796d2a71");
//        taskService.complete("398a7470-d3ce-11ea-8bb4-dcfb4875e032");
        System.out.println("完成任务");
    }
}
