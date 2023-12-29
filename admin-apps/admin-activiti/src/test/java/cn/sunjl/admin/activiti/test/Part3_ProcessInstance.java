package cn.sunjl.admin.activiti.test;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Part3_ProcessInstance {

    @Autowired
    private RuntimeService runtimeService;
    // 创建实例
    @Test
    public void createInstance(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("part6_uel_v1:1:861f9909-9d56-11ee-8240-f426796d2a7", "1111");
        System.out.println("流程实例ID："+processInstance.getProcessDefinitionId());

    }
    // 查询所有实例
    @Test
    public void getAllInstantce(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        for(ProcessInstance pi : list){
            System.out.println("实例id："+pi.getId());
            System.out.println("实例对应的定义实例："+pi.getProcessDefinitionId());
            System.out.println("实例对应的业务id："+pi.getBusinessKey());
            System.out.println("实例名字："+pi.getName());
            System.out.println("实例是否开启："+pi.isEnded());
            System.out.println("实例是否挂起：："+pi.isSuspended());
        }
    }
    // 挂起实例 挂起后 后面都无法执行了
    @Test
    public void suspendedInstance(){
        runtimeService.suspendProcessInstanceById("4600838c-9986-11ee-987d-f426796d2a71");
    }
    // 激活实例
    @Test
    public void activeInstance(){
        runtimeService.activateProcessInstanceById("4600838c-9986-11ee-987d-f426796d2a71");
    }
    // 删除实例
    @Test
    public void delInstance(){
        runtimeService.deleteProcessInstance("5bf30036-9af9-11ee-aaa9-f426796d2a71","测试删除");
    }
}
