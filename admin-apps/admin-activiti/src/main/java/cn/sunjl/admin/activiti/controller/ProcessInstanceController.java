package cn.sunjl.admin.activiti.controller;


import cn.sunjl.admin.activiti.api.UserApi;
import cn.sunjl.admin.activiti.dto.VaribleDTO;
import cn.sunjl.admin.activiti.entity.QueryProcessInstance;
import cn.sunjl.admin.activiti.pojo.FormData;
import cn.sunjl.admin.activiti.service.FormDataService;

import cn.sunjl.admin.authority.entity.auth.UserExt;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;

import cn.sunjl.admin.log.annotation.SysLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Validated
@RestController
@RequestMapping("/instance")
@Api(tags = "流程实例", value = "processInstance")
@Slf4j
public class ProcessInstanceController extends BaseController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private FormDataService formDataService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private UserApi userApi;
    // 获取当前用户的流程实例
    @SysLog("获取当前用户发起的流程实例")
    @RequestMapping(value = "/getProcessInstanceByUser", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户发起的流程实例", notes = "获取当前用户发起的流程实例")
    public R getProcessInstanceByUser(){
        String account = getAccount();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().startedBy(account).list();
//        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().startedBy(String.valueOf(userId)).list();
        return R.success(list);
    }



    // 查询所有流程实例
    @SysLog("查询所有流程实例")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "查询流程实例", notes = "查询流程实例")
    public R getAllProcessInstance() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();

        List<QueryProcessInstance> plist = new ArrayList<>();
        for (ProcessInstance pi : list) {
            QueryProcessInstance ins = converProInstance(pi);

            //todo  这里还需要查询一个参数 resourceName 和 deploymentid 必须用其他方法查询
            // 因为runtimeService("流程部署ID")查询的结果没有部署流程与部署ID，所以用repositoryService查询
            //                ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
            //                        .processDefinitionId(pi.getProcessDefinitionId())
            //                        .singleResult();
            //                hashMap.put("resourceName", pd.getResourceName());
            //                hashMap.put("deploymentId", pd.getDeploymentId());
            plist.add(ins);
        }
        return R.success(plist);
    }

    // 分页查询所有流程实例
    @SysLog("分页查询所有流程实例")
    @RequestMapping(value = "/pageList", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询流程实例", notes = "分页查询流程实例")
    public R getAllProcessInstanceByPage(@RequestParam @ApiParam(name = "page", value = "页码") int page,
                                    @RequestParam @ApiParam(name = "size", value = "每页数量") int size) {
        if (page <= 1) {
            page = 0;
        } else {
            page = (page - 1) * size;
        }


        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().listPage(page,size);
        List<QueryProcessInstance> plist = new ArrayList<>();
        for (ProcessInstance pi : list) {
            QueryProcessInstance ins = converProInstance(pi);
            //todo  这里还需要查询一个参数 resourceName 和 deploymentid 必须用其他方法查询
            // 因为runtimeService("流程部署ID")查询的结果没有部署流程与部署ID，所以用repositoryService查询
            //                ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
            //                        .processDefinitionId(pi.getProcessDefinitionId())
            //                        .singleResult();
            //                hashMap.put("resourceName", pd.getResourceName());
            //                hashMap.put("deploymentId", pd.getDeploymentId());
            plist.add(ins);
        }
        List<ProcessInstance> list1 = runtimeService.createProcessInstanceQuery().list();

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("pagelist", plist);
        resMap.put("total", list1.size());
        return R.success(resMap);
    }


    // 启动流程实例  processinstanceId 是 ACT_RE_PROCDEF 里的 id
    // @RequestParam() @ApiParam(name = "businessId", value = "业务ID") String businessId
    @SysLog("启动流程实例")
    @RequestMapping(value = "/startInstanceById", method = RequestMethod.GET)
    @ApiOperation(value = "无参启动实例", notes = "通过ID启动实例")
    public R startInstance(
            @RequestParam() @ApiParam(name = "processInstatnceId", value = "流程实例的id()") String processInstatnceId) {
        String account = getAccount();
        Authentication.setAuthenticatedUserId(String.valueOf(account));
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processInstatnceId);
        return R.success(converProInstance(processInstance));
    }

    @SysLog("带参数启动流程实例")
    @RequestMapping(value = "/startInstanceByIdAndVariable", method = RequestMethod.POST)
    @ApiOperation(value = "有参启动实例", notes = "通过ID和参数启动实例")
    public R startInstanceByIdAndVariable(
            @RequestParam() @ApiParam(name = "processInstatnceId", value = "流程实例的id()") String processInstatnceId,
            @RequestBody() @ApiParam(name = "varibles",value = "启动流程实例的参数")List<VaribleDTO> varibles) {
        String account = getAccount();
        Authentication.setAuthenticatedUserId(String.valueOf(account));
        Map<String,Object> map = new HashMap<>();
        for(VaribleDTO var : varibles){
            System.out.println(var.getKey());
            System.out.println(var.getValue());
            map.put(var.getKey(),var.getValue());
        }
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processInstatnceId,map);
        return R.success(converProInstance(processInstance));
    }


    // 挂起流程实例
    @SysLog("挂起流程")
    @RequestMapping(value = "/suspendInstance", method = RequestMethod.GET)
    @ApiOperation(value = "挂起实例", notes = "把流程实例暂时挂起")
    public R suspendInstance(@RequestParam()@ApiParam(name = "processInstatnceId",value = "流程实例的id") String processInstatnceId ){
        runtimeService.suspendProcessInstanceById(processInstatnceId);
        return R.success().setMsg("挂起成功");
    }

    // 激活流程实例
    @SysLog("激活流程")
    @RequestMapping(value = "/actInstance", method = RequestMethod.GET)
    @ApiOperation(value = "激活实例", notes = "把挂起的流程重新激活下")
    public R actInstance(@RequestParam()@ApiParam(name = "processInstatnceId",value = "流程实例的id") String processInstatnceId){
        runtimeService.activateProcessInstanceById(processInstatnceId);
        return R.success().setMsg("激活成功");
    }
    // 重启流程实例

    // 删除流程实例
    @SysLog("删除流程")
    @RequestMapping(value = "/delInstatnce", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除实例", notes = "删除流程实例")
    @Transactional
    public R delInstatnce(@RequestParam()@ApiParam(name = "processInstatnceId",value = "流程实例的id") String processInstatnceId,@RequestParam()@ApiParam(name = "reason",value = "删除流程实例备注")String reason){
        runtimeService.deleteProcessInstance(processInstatnceId,reason);
        //同时删除formDate表里的则个流程相关内容
        QueryWrapper<FormData> wrapper = new QueryWrapper<>();
        wrapper.eq("PROC_INST_ID_",processInstatnceId);
        formDataService.remove(wrapper);
        return R.success().setMsg("删除成功");
    }
    //查询流程参数
    @SysLog("获取实例参数")
    @RequestMapping(value = "/variables", method = RequestMethod.GET)
    @ApiOperation(value = "获取实例参数", notes = "获取实例的参数")
    public R variables(@RequestParam() @ApiParam(name = "processInstatnceId",value = "流程实例的id") String processInstatnceId){
        Map<String, Object> variables = runtimeService.getVariables(processInstatnceId);
        Set<Map.Entry<String, Object>> entries = variables.entrySet();
//        for (Map.Entry<String, Object> entry : entries) {
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue());
//        }
        return R.success(variables);
    }

    // processinstatnce 转化类
    private QueryProcessInstance converProInstance(ProcessInstance processInstance) {

        QueryProcessInstance proins = new QueryProcessInstance();
        proins.setDeploymentId(processInstance.getDeploymentId());
        proins.setProcessVariables(processInstance.getProcessVariables());
        proins.setName(processInstance.getName());
        proins.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        proins.setProcessInstatnceId(processInstance.getId());
        proins.setBusinessKey(processInstance.getBusinessKey());
        proins.setDescription(processInstance.getDescription());
        proins.setLocalizedDescription(processInstance.getLocalizedDescription());
        proins.setLocalizedName(processInstance.getLocalizedName());
        proins.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        proins.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        proins.setProcessDefinitionVersion(processInstance.getProcessDefinitionVersion());
        proins.setStartTime(processInstance.getStartTime());
        proins.setStartUserId(processInstance.getStartUserId());
        proins.setTenantId(processInstance.getTenantId());
        proins.setActivityId(processInstance.getActivityId());
        proins.setParentId(processInstance.getParentId());
        proins.setParentProcessInstanceId(processInstance.getParentProcessInstanceId());
        proins.setProcessInstanceId(processInstance.getProcessInstanceId());
        proins.setRootProcessInstanceId(processInstance.getRootProcessInstanceId());
        proins.setSuperExecutionId(processInstance.getSuperExecutionId());
        proins.setSuspended(processInstance.isSuspended());
        proins.setEnded(processInstance.isEnded());

        if (processInstance.getStartUserId()!=null && processInstance.getStartUserId() !=""){
            R<UserExt> r = userApi.getUserByAccount(processInstance.getStartUserId());
            proins.setStarInstatncetUser(r.getData().getName());
        }
        return proins;
    }
}
