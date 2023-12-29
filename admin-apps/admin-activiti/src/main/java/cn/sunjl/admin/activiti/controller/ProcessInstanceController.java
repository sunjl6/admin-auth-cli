package cn.sunjl.admin.activiti.controller;

import cn.sunjl.admin.activiti.entity.QueryProcessInstance;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private DozerUtils dozerUtils;

    // 查询所有流程实例
    @SysLog("查询所有流程实例")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "查询流程实例", notes = "查询流程实例")
    public R getAllProcessInstance() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        List<QueryProcessInstance> plist = new ArrayList<>();
        System.out.println(list);
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
    @ApiOperation(value = "通过ID启动实例", notes = "启动实例")
    public R startInstance(
            @RequestParam() @ApiParam(name = "processInstatnceId", value = "流程实例的id()") String processInstatnceId) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processInstatnceId);
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
    public R delInstatnce(@RequestParam()@ApiParam(name = "processInstatnceId",value = "流程实例的id") String processInstatnceId,@RequestParam()@ApiParam(name = "reason",value = "删除流程实例备注")String reason){
        runtimeService.deleteProcessInstance(processInstatnceId,reason);
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
        return proins;
    }
}
