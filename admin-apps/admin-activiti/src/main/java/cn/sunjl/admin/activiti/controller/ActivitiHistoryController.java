package cn.sunjl.admin.activiti.controller;

import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@RequestMapping("/history")
@Api(tags = "历史记录", value = "历史记录查询")
@Slf4j
public class ActivitiHistoryController extends BaseController {

    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;


    // 用户历史任务查询
    @SysLog("用户历史任务查询")
    @RequestMapping(value = "/taskHistoryByAssignee", method = RequestMethod.GET)
    @ApiOperation(value = "用户历史task", notes = "查询获取用户的历史task")
    public R taskHistoryByAssignee() {
        String account = getAccount();
//        account = "bajie"; //测试用
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
                .taskAssignee(account)
                .list();

        return R.success(list);
    }

    //根据流程实例ID查询任务
    @SysLog("根据流程id查询任务")
    @RequestMapping(value = "/taskHistoryByProcessId", method = RequestMethod.GET)
    @ApiOperation(value = "流程id历史task", notes = "流程id查询历史task")
    public R taskHistoryByProcessId(@RequestParam()@ApiParam(name = "processInstanceId",value = "流程实例id")String processInstanceId){
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
                .processInstanceId(processInstanceId)
                .list();

        return R.success(list);
    }
    // 高亮显示历史流程
    @SysLog("获取需要高亮的元素")
    @RequestMapping(value = "/getHighLine", method = RequestMethod.GET)
    @ApiOperation(value = "高亮渲染流程", notes = "通过实例id获取流程图需要高亮显示的元素")
    public R getHighLine(@RequestParam()@ApiParam(name = "processInstanceId",value = "流程实例id")String processInstanceId){
        // 通过流程id 参数 获取 他的历史记录
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 通过流程定义的id 获取BPMN
        if (historicProcessInstance == null){
            return R.fail("请确认流程id是否正确");
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

        // 通过BPMN Model 获取 process
        Process process = bpmnModel.getProcesses().get(0);
        // 获取所有流程元素信息
        Collection<FlowElement> flowElementsList = process.getFlowElements();
        Map<String,String> map = new HashMap<>(); // 获取所有箭头的元素的map
        for (FlowElement fle : flowElementsList){
            // 获取所有线的节点信息
            if (fle instanceof SequenceFlow){
                SequenceFlow se = (SequenceFlow) fle;
                // 获取箭头的 去的放行的id
                String targetRef = se.getTargetRef();
                // 获取箭头来源的 元素id
                String sourceRef = se.getSourceRef();
                // 全部放到map里
                map.put(sourceRef+targetRef,se.getId());
            }
        }

        //流程走过的各个节点的 id 组合： 例如 开始节点id 和 流程1 ，流程1和开始， 开始和流程2 流程2和开始， 流程1和流程2 ，流程2和流程1 。。。。
        List<HistoricActivityInstance> historyList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        Set<String> keyset = new HashSet<>(); // 获取了所有走过的节点之间关系的map
        for(HistoricActivityInstance i : historyList){
            for(HistoricActivityInstance j :historyList){
                if ( i != j){
                    keyset.add(i.getActivityId()+j.getActivityId());
                }
            }
        }

        //找出需要高亮的连线，也就是判断那些连线已经走过了 ，那么上面历史keyset 里有存放走过的节点信息
        // 这里主要是 拿走过的节点22对应的 字符串和 所有线22对应的字符串做 刷选，如果有一样的那么说明这条线是走过的。那么放到一个新的集合里等带返回给前端
        Set<String> highLine = new HashSet<>();
        for(String s : keyset){
            Set<Map.Entry<String, String>> entries = map.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if (s.equals(entry.getKey())){
                    highLine.add(entry.getKey());
                }
            }
        }
        // 获取已经完成的节点高亮
        List<HistoricActivityInstance> finishedList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).finished().list();
        Set<String> highPoint = new HashSet<>();
        for (HistoricActivityInstance h : finishedList){
            highPoint.add(h.getActivityId());
        }

        // 未完成节点高亮
        List<HistoricActivityInstance> unfinishedList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).unfinished().list();
        Set<String> highUnfinishedPoint = new HashSet<>();
        for (HistoricActivityInstance h : unfinishedList){
            highUnfinishedPoint.add(h.getActivityId());
        }

        // // 当前用户完成的任务高亮
        String account = getAccount();
//        account = "bajie"; // 测试用
        List<HistoricTaskInstance> currentUserFinishedTask = historyService.createHistoricTaskInstanceQuery().taskAssignee(account).processInstanceId(processInstanceId).finished().list();
        Set<String> UserFinishedTask = new HashSet<>();
        for (HistoricTaskInstance htk : currentUserFinishedTask){
            UserFinishedTask.add(htk.getTaskDefinitionKey());
        }

        Map<String,Object> resMap = new HashMap<>();
        resMap.put("highLine",highLine);
        resMap.put("highPoint",highPoint);
        resMap.put("unDoPoint",highUnfinishedPoint);
        resMap.put("userFinishedTask",UserFinishedTask);

        return R.success(resMap);
    }
}
