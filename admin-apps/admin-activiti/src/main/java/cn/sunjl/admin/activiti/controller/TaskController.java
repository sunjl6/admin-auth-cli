package cn.sunjl.admin.activiti.controller;

import cn.sunjl.admin.activiti.entity.QueryTask;
import cn.sunjl.admin.activiti.pojo.FormData;
import cn.sunjl.admin.activiti.pojo.IdentityLink;
import cn.sunjl.admin.activiti.service.FormDataService;
import cn.sunjl.admin.activiti.service.IdentityLinkService;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.log.annotation.SysLog;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/task")
@Api(tags = "任务", value = "task")
@Slf4j
public class TaskController extends BaseController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRuntime taskRuntime;
    @Autowired
    private IdentityLinkService identityLinkService;
    // 获取当前候选人任务列表 candidate
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private FormDataService formDataService;

    @SysLog("获取当前候选的task，也就是可以拾取的任务")
    @RequestMapping(value = "/candidateTask", method = RequestMethod.GET)
    @ApiOperation(value = "候选人task", notes = "当前候选人能claim获取到的task")
    public R getCandidateTask(){
        String userAccount = getAccount();
        userAccount = "sunjl"; // 测试用
        QueryWrapper<IdentityLink> wrapper = new QueryWrapper<>();
        wrapper.eq("TYPE_","candidate").eq("USER_ID_",userAccount);
        List<IdentityLink> list = identityLinkService.list(wrapper);
        List<QueryTask> queryTasks = new ArrayList<>();
        for (IdentityLink idlk : list){
            String taskid = idlk.getTaskId();
            Task task = taskService.createTaskQuery().taskId(taskid).singleResult();
            QueryTask queryTask = taskConvert(task);
            queryTasks.add(queryTask);

        }
        return R.success(queryTasks);
    }

    // 获取当前用户的任务列表
    @SysLog("获取当前用户的task")
    @RequestMapping(value = "/userTask", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户所有task", notes = "获取用户task")
    public R getUserTask(){
        String userAccount = getAccount();
        userAccount = "bajie"; // 测试用
//        taskService.createTaskQuery().taskAssignee()
        List<Task> tasks = taskService.createTaskQuery().list();
        System.out.println(tasks);
        List<QueryTask> qtskList = new ArrayList<>();
        for(Task ts : tasks){
            if (ts.getAssignee() != null && ts.getAssignee().contains(userAccount)){
                QueryTask queryTask = taskConvert(ts);
                qtskList.add(queryTask);
            }
        }

        return R.success(qtskList);
    }
    //完成任务
    @SysLog("完成任务")
    @RequestMapping(value = "/finsihTask", method = RequestMethod.GET)
    @ApiOperation(value = "完成任务", notes = "完成task任务")
    public R finishTask(@RequestParam()@ApiParam(name = "taskId",value = "需要传入taskid")String taskId){
        String account = getAccount();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        account = "bajie";
        if (task == null) {
            return R.fail("没用这个task");
        }else if(!task.getAssignee().contains(account)){
            return R.fail("请确认当前用户是否能完成次任务");
        }
        else {
            taskService.complete(taskId);
            return R.success();
        }
    }

    // 拾取任务
    @SysLog("拾取任务")
    @RequestMapping(value = "/claimTask", method = RequestMethod.GET)
    @ApiOperation(value = "拾取任务", notes = "候选人拾取任务")
    public R claimTask(@RequestParam()@ApiParam(name = "taskId",value = "需要传递任务id参数") String taskId){
        String account = getAccount();
        account="sunjl"; //测试用
        // 拾取任务之前先查询下当前用户是不是有候选任务task ，不能随便谁都能拾取任务
        QueryWrapper<IdentityLink> wrapper = new QueryWrapper<>();
        wrapper.eq("TYPE_", "candidate").eq("USER_ID_", account).eq("TASK_ID_", taskId);
        List<IdentityLink> list = identityLinkService.list(wrapper);
        if (list== null && list.size()<=0){
            return R.fail("获取任务失败，确认是否是当前用户");
        }
        taskService.claim(taskId,account);
        return R.success().setMsg("获取任务");
    }


    // 归还任务
    @SysLog("归还任务")
    @RequestMapping(value = "/claimBackTask", method = RequestMethod.GET)
    @ApiOperation(value = "归还任务", notes = "候选人拾取任务后归还任务")
    public R claimBackTask(@RequestParam()@ApiParam(name = "taskId",value = "需要传递任务id参数") String taskId){
        String account = getAccount();
        account="sunjl";
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (!task.getAssignee().contains(account)){
            return R.fail("归还任务失败，不是当前用户");
        }
        taskService.setAssignee(taskId,null);
        return R.success().setMsg("归还任务成功");
    }

    // 指派一个新的用户做审批人，既不是候选人，也不是原来的执行人
    @SysLog("任务指派给新assignee")
    @RequestMapping(value = "/assigneNewUser", method = RequestMethod.GET)
    @ApiOperation(value = "任务指派新assignee", notes = "任务指派一个新的用户做审批人，既不是候选人，也不是原来的执行人")
    public R assigneNewUser(@RequestParam()@ApiParam(name = "taskId",value = "需要传递任务id参数") String taskId,
                            @RequestParam()@ApiParam(name="newAssigneeAccount",value = "新指派人的用户账号") String newAssigneeAccount){
        String account = getAccount();
        account="sunjl";// 测试名字
        QueryWrapper<IdentityLink> wrapper = new QueryWrapper<>();
        wrapper.eq("TYPE_", "candidate").like("USER_ID_", account).eq("TASK_ID_", taskId);
        List<IdentityLink> list = identityLinkService.list(wrapper);
        if (list!= null && list.size()>0){
            taskService.setAssignee(taskId,newAssigneeAccount);
            return R.success().setMsg("指派"+newAssigneeAccount+"成功");

        }
        return R.fail("指派失败，确认是否是当前用户");
    }

    // 渲染动态表单
    @SysLog("渲染表单")
    @RequestMapping(value = "/formDataRender", method = RequestMethod.GET)
    @ApiOperation(value = "渲染表单", notes = "bpmn动态渲染表单")
    public R formDataRender(@RequestParam()@ApiParam(name = "taskId",value = "任务id")String taskId)throws IOException {
        // 获取task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null){
            return R.fail("请确认taskid是否正确");
        }
        // 构建历史表单数据字典
        HashMap<String,String> tempMap = new HashMap<>();
        QueryWrapper<FormData> wrapper = new QueryWrapper<>();
        wrapper.eq("PROC_INST_ID_",task.getProcessInstanceId());
        List<FormData> formDataHistoryList = formDataService.list(wrapper);
        for(FormData fd : formDataHistoryList){
            tempMap.put(fd.getControlId(),fd.getControlValue());
        }

        //获取userTask 里面有我们需要的属性
        UserTask userTask = (UserTask) repositoryService.getBpmnModel(task.getProcessDefinitionId()).getFlowElement(task.getFormKey());
        // 判断下nulll
        if (userTask == null){
            return R.success().setMsg("无表单");
        }
        List<Map<String,Object>> responseList = new ArrayList<>();
        // 遍历所有表单并且转成前端认识的字符
        List<FormProperty> formProperties = userTask.getFormProperties();
        for (FormProperty fp : formProperties){
            String[] split = fp.getId().split("-_!");
            Map<String,Object> map = new HashMap<>();
            map.put("id",split[0]);
            map.put("controlType",split[1]);
            map.put("controlLabel",split[2]);
            // 判断流程设计的时候 参数的默认值是某个分支的 表单id开头的说明 这里需要一个默认值是别的分支的数据
            if (split[3].startsWith("FormProperty_")){
                // 如果是FormProperty开头的 就去 map 里把这个key 的值拿出来给前端渲染
                //这里再做一个判断 如果tempMap 里拿不到 东西，说明传入的 值有问题
                if(tempMap.containsKey(split[3])){
                    map.put("controlDefaultValue",tempMap.get(split[3]));
                }else{
                    map.put("controlDefaultValue","读取失败,检查"+split[0]+"配置");
                }
            }else{
                // 说明不是 拿就 把原来拆分的字段的值放进去
                map.put("controlDefaultValue",split[3]);
            }
            map.put("controleParam",split[4]);
            responseList.add(map);
        }

        return R.success(responseList);
    }



    // 保存动态表单
    @SysLog("保存动态表单")
    @RequestMapping(value = "/formDataSave", method = RequestMethod.POST)
    @ApiOperation(value = "保存动态表单", notes = "保存动态表单到数据库")
    public R formDataSave(@RequestParam()@ApiParam(name = "taskid",value = "任务的id")String taskid,
                          @RequestParam()@ApiParam(name = "formData",value = "表单数据")String formData) throws Exception{
        Task task = taskService.createTaskQuery().taskId(taskid).singleResult();
        if (task == null){
            return R.fail("请确认taskid是否正确");
        }
        String[] formDatalist = formData.split("!_!");
//        System.out.println(task);
        //formData:控件id-_!控件值-_!是否参数!_!控件id-_!控件值-_!是否参数
        //FormProperty_0lovri0-_!不是参数-_!f!_!FormProperty_1iu6onu-_!数字参数-_!s

        HashMap<String, Object> variables = new HashMap<String, Object>();
        Boolean hasVariables = false;//没有任何参数

        List<Map<String,Object>> resFormDataList = new ArrayList<>();
        List<FormData> saveSqlFromDatas = new ArrayList<>();
        for(String s : formDatalist){
            FormData fd = new FormData();
            Map<String,Object> map = new HashMap<>();
            String[] formDataItems = s.split("-_!");
            map.put("PROC_INST_ID_",task.getProcessInstanceId());
            fd.setProcessInstanceId(task.getProcessInstanceId());
            map.put("PROC_DEF_ID_",task.getProcessDefinitionId());
            fd.setProcessDefinitionId(task.getProcessDefinitionId());
            map.put("FORM_KEY_",task.getFormKey());
            fd.setFormKey(task.getFormKey());
            map.put("Control_ID_",formDataItems[0]);
            fd.setControlId(formDataItems[0]);
            map.put("Control_VALUE_",formDataItems[1]);
            fd.setControlValue(formDataItems[1]);
            map.put("Control_PARAM_",formDataItems[2]);
            saveSqlFromDatas.add(fd);
            resFormDataList.add(map);
            //构建参数集合
            switch (formDataItems[2]) {
                case "f":
                    System.out.println("控件值不作为参数");
                    break;
                case "s":
                    variables.put(formDataItems[0], formDataItems[1]);
                    hasVariables = true;
                    break;
                case "t":
                    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    variables.put(formDataItems[0], timeFormat.parse(formDataItems[2]));
                    hasVariables = true;
                    break;
                case "b":
                    variables.put(formDataItems[0], BooleanUtils.toBoolean(formDataItems[2]));
                    hasVariables = true;
                    break;
                default:
                    System.out.println("控件参数类型配置错误：" + formDataItems[0] + "的参数类型不存在，" + formDataItems[2]);
            }
        }

        // 如果有参数 那么就完成task
        if (hasVariables) {
            //带参数完成任务
            taskService.complete(taskid,variables);
        } else {
            taskService.complete(taskid);
        }
        // 保存到数据库
        formDataService.saveBatch(saveSqlFromDatas);
        return R.success(resFormDataList);
    }

    // task 转成成 查询对象传递到前端
    private QueryTask taskConvert(Task task){
        QueryTask qts = new QueryTask();
        qts.setDelegationState(task.getDelegationState());
        qts.setAssignee(task.getAssignee());
        qts.setCategory(task.getCategory());
        qts.setDescription(task.getDescription());
        qts.setTaskId(task.getId());
        qts.setClaimTime(task.getClaimTime());
        qts.setTaskCreateTime(task.getCreateTime());
        qts.setDueDate(task.getDueDate());
        qts.setExecutionId(task.getExecutionId());
        qts.setFormKey(task.getFormKey());
        qts.setName(task.getName());
        qts.setOwner(task.getOwner());
        qts.setParentTaskId(task.getParentTaskId());
        qts.setPriority(task.getPriority());
        qts.setProcessDefinitionId(task.getProcessDefinitionId());
        qts.setProcessInstanceId(task.getProcessInstanceId());
        qts.setTaskLocalVariables(task.getTaskLocalVariables());
        qts.setTaskDefinitionKey(task.getTaskDefinitionKey());
        qts.setTaskLocalVariables(task.getTaskLocalVariables());
        qts.setTenantId(task.getTenantId());
        return qts;
    }
}
