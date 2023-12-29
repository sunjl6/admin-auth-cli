package cn.sunjl.admin.activiti.test;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Part8_ProcessRuntime {
    @Autowired
    private ProcessRuntime processRuntime;

    @Test
    public void getProcessInstance(){
        Page<ProcessInstance> processInstancePage = processRuntime
                .processInstances(Pageable.of(0,100));
        System.out.println("流程实例数量："+processInstancePage.getTotalItems());
        List<ProcessInstance> list = processInstancePage.getContent();
        for(ProcessInstance pi : list){
            System.out.println("-----------------------");
            System.out.println("getId：" + pi.getId());
            System.out.println("getName：" + pi.getName());
            System.out.println("getStartDate：" + pi.getStartDate());
            System.out.println("getStatus：" + pi.getStatus());
            System.out.println("getProcessDefinitionId：" + pi.getProcessDefinitionId());
            System.out.println("getProcessDefinitionKey：" + pi.getProcessDefinitionKey());

        }
    }
}
