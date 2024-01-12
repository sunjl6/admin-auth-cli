package cn.sunjl.admin.activiti.test;

import cn.sunjl.admin.activiti.api.ResourceApi;
import cn.sunjl.admin.activiti.api.UserApi;
import cn.sunjl.admin.activiti.pojo.ActRuVariable;
import cn.sunjl.admin.activiti.service.ActRuVariableService;
import cn.sunjl.admin.authority.entity.auth.UserExt;
import cn.sunjl.admin.base.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.Execution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class feignTest {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ActRuVariableService actRuVariableService;

    @Test
    public void test(){

        runtimeService.signalEventReceived("Signal_0et7rco");

    }

    @Test
    public void testMessageRecive(){

//        Execution excu = runtimeService.createExecutionQuery().messageEventSubscriptionName("Message_2ul8kgg").processInstanceId("8c1b3ad1-b079-11ee-a547-f426796d2a6d").singleResult();
//        System.out.println(excu.getId());
//
//        runtimeService.messageEventReceived("Message_2ul8kgg",excu.getId());
        Execution excu1 = runtimeService.createExecutionQuery().messageEventSubscriptionName("Message_2aclnkt").processInstanceId("0e073d26-b07d-11ee-a547-f426796d2a6d").singleResult();
        System.out.println(excu1.getId());
    }

    @Test
    public void testMessageRecive2(){
        String processInstanceId = "277f6a1b-b083-11ee-a286-f426796d2a6d";
        List<HistoricActivityInstance> unfinishedList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).unfinished().list();
        System.out.println(unfinishedList.size());
    }
}


