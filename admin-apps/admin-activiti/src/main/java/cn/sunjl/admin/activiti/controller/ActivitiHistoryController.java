package cn.sunjl.admin.activiti.controller;

import cn.sunjl.admin.activiti.dto.HistoricProcessInstancePageDTO;
import cn.sunjl.admin.activiti.dto.TimeLineDTO;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.log.annotation.SysLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping("/history")
@Api(tags = "历史记录", value = "历史记录查询")
@Slf4j
public class ActivitiHistoryController extends BaseController {

    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;

    //封装一个ElementUI查看流程进度可用使用的流程查看流程进度图的结果信息
    @SysLog("ElementUI查看流程进度")
    @RequestMapping(value = "/progressForElementUI", method = RequestMethod.GET)
    @ApiOperation(value = "ElementUI查看流程进度", notes = "ElementUI查看流程进度")
    public R progressForElementUI(@RequestParam @ApiParam(name = "processInstanceId", value = "流程实例id") String processInstanceId) {
        // 通过流程id 参数 获取 他的历史记录
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 通过流程定义的id 获取BPMN
        if (historicProcessInstance == null) {
            return R.fail("请确认流程id是否正确");
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
        // 通过BPMN Model 获取 process
        Process process = bpmnModel.getProcesses().get(0);
        // 获取所有流程元素信息
        Collection<FlowElement> flowElementsList = process.getFlowElements();
        // 获取所有审批流的节点信息
        List actList = new ArrayList();
        for (FlowElement fle : flowElementsList) {
            // 获取开始名称
            if (fle instanceof StartEvent) {
                StartEvent  start = (StartEvent)fle;

                actList.add( start.getName()==null? "开始":start.getName());
            }

            // 获取所有节点名称
            if (fle instanceof Activity) {
                Activity ac = (Activity) fle;
                String name = ac.getName();
                actList.add(name);
            }
            // 获取结束流程名称
            if(fle instanceof EndEvent){
                EndEvent  End = (EndEvent)fle;
                actList.add(End.getName()==null? "结束":End.getName());
            }
        }
        // 创建一个返回数据的list集合
        List<TimeLineDTO> resList = new ArrayList<>();

        // 查询已经完成的节点准备封装信息
        List<HistoricActivityInstance> finishedActList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).finished().list();


        // 遍历所有节点的信息 构造返回数据
        for (Object o : actList) {
            String name = o.toString();
            List<HistoricActivityInstance> list = finishedActList.stream().filter(his -> name.equals(his.getActivityName())).collect(Collectors.toList());
            if(list.size()<=0){
                TimeLineDTO timeLineDTO = new TimeLineDTO();
                timeLineDTO.setName(name);
                resList.add(timeLineDTO);
            }else{
                TimeLineDTO timeLineDTO = new TimeLineDTO();
                HistoricActivityInstance historicActivityInstance = list.get(0);
                timeLineDTO.setName(historicActivityInstance.getActivityName());
                timeLineDTO.setStartTime(historicActivityInstance.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                timeLineDTO.setEndTime(historicActivityInstance.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                timeLineDTO.setAssignee(historicActivityInstance.getAssignee());
                resList.add(timeLineDTO);
            }

        }


        return R.success(resList);
    }

    // 分页查询所有任务流程
    @SysLog("分页查询所有历史任务")
    @RequestMapping(value = "/pageTaskHistory", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询所有历史任务", notes = "分页查询所有历史任务")
    public R pageTaskHistory(@RequestParam @ApiParam(name = "page", value = "页码") int page,
                             @RequestParam @ApiParam(name = "size", value = "每页显示数") int size) {
        if (page <= 1) {
            page = 0;
        } else {
            page = (page - 1) * size;
        }

        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().listPage(page, size);
        List<HistoricTaskInstance> list1 = historyService.createHistoricTaskInstanceQuery().list();
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("pagelist", historicTaskInstances);
        resMap.put("total", list1.size());
        return R.success(resMap);
    }

    //
    @SysLog("分页查询所有历史流程")
    @RequestMapping(value = "/pageProcessHistory", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询所有历史流程", notes = "分页查询所有历史流程")
    public R pageProcessInstanceHistory(@RequestParam @ApiParam(name = "page", value = "页码") int page,
                                        @RequestParam @ApiParam(name = "size", value = "每页显示数") int size) {
        if (page <= 1) {
            page = 0;
        } else {
            page = (page - 1) * size;
        }

        List<HistoricProcessInstance> pagelist = historyService.createHistoricProcessInstanceQuery()
                .listPage(page, size);

        List<HistoricProcessInstance> list1 = historyService.createHistoricProcessInstanceQuery().list();

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("pagelist", pagelist);
        resMap.put("total", list1.size());
        return R.success(resMap);
    }

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
    public R taskHistoryByProcessId(@RequestParam() @ApiParam(name = "processInstanceId", value = "流程实例id") String processInstanceId) {
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
    public R getHighLine(@RequestParam() @ApiParam(name = "processInstanceId", value = "流程实例id") String processInstanceId) {
        // 通过流程id 参数 获取 他的历史记录
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 通过流程定义的id 获取BPMN
        if (historicProcessInstance == null) {
            return R.fail("请确认流程id是否正确");
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

        // 通过BPMN Model 获取 process
        Process process = bpmnModel.getProcesses().get(0);
        // 获取所有流程元素信息
        Collection<FlowElement> flowElementsList = process.getFlowElements();
        Map<String, String> map = new HashMap<>(); // 获取所有箭头的元素的map
        for (FlowElement fle : flowElementsList) {
            // 获取所有线的节点信息
            if (fle instanceof SequenceFlow) {
                SequenceFlow se = (SequenceFlow) fle;
                // 获取箭头的 去的放行的id
                String targetRef = se.getTargetRef();
                // 获取箭头来源的 元素id
                String sourceRef = se.getSourceRef();
                // 全部放到map里
                map.put(sourceRef + targetRef, se.getId());
            }
        }

        //流程走过的各个节点的 id 组合： 例如 开始节点id 和 流程1 ，流程1和开始， 开始和流程2 流程2和开始， 流程1和流程2 ，流程2和流程1 。。。。
        List<HistoricActivityInstance> historyList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        Set<String> keyset = new HashSet<>(); // 获取了所有走过的节点之间关系的map
        for (HistoricActivityInstance i : historyList) {
            for (HistoricActivityInstance j : historyList) {
                if (i != j) {
                    keyset.add(i.getActivityId() + j.getActivityId());
                }
            }
        }

        //找出需要高亮的连线，也就是判断那些连线已经走过了 ，那么上面历史keyset 里有存放走过的节点信息
        // 这里主要是 拿走过的节点22对应的 字符串和 所有线22对应的字符串做 刷选，如果有一样的那么说明这条线是走过的。那么放到一个新的集合里等带返回给前端
        Set<String> highLine = new HashSet<>();
        for (String s : keyset) {
            Set<Map.Entry<String, String>> entries = map.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if (s.equals(entry.getKey())) {
                    highLine.add(entry.getKey());
                }
            }
        }
        // 获取已经完成的节点高亮
        List<HistoricActivityInstance> finishedList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).finished().list();
        Set<String> highPoint = new HashSet<>();
        for (HistoricActivityInstance h : finishedList) {
            highPoint.add(h.getActivityId());
        }

        // 未完成节点高亮
        List<HistoricActivityInstance> unfinishedList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).unfinished().list();
        Set<String> highUnfinishedPoint = new HashSet<>();
        for (HistoricActivityInstance h : unfinishedList) {
            highUnfinishedPoint.add(h.getActivityId());
        }

        // // 当前用户完成的任务高亮
        String account = getAccount();
//        account = "bajie"; // 测试用
        List<HistoricTaskInstance> currentUserFinishedTask = historyService.createHistoricTaskInstanceQuery().taskAssignee(account).processInstanceId(processInstanceId).finished().list();
        Set<String> UserFinishedTask = new HashSet<>();
        for (HistoricTaskInstance htk : currentUserFinishedTask) {
            UserFinishedTask.add(htk.getTaskDefinitionKey());
        }

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("highLine", highLine);
        resMap.put("highPoint", highPoint);
        resMap.put("unDoPoint", highUnfinishedPoint);
        resMap.put("userFinishedTask", UserFinishedTask);

        return R.success(resMap);
    }


}
