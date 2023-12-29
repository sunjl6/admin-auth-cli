package cn.sunjl.admin.activiti.controller;

import cn.sunjl.admin.activiti.dto.Bpm;
import cn.sunjl.admin.activiti.entity.QueryDeployment;
import cn.sunjl.admin.activiti.entity.QueryProcessDefinition;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Validated
@RestController
@RequestMapping("/definition")
@Api(tags = "流程定义", value = "definition")
@Slf4j
public class ProcessDefinitionController extends BaseController {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private DozerUtils dozerUtils;
    @Autowired
    private RuntimeService runtimeService;
    // 下载bpmn文件
    @SysLog("下载BPMN文件")
    @RequestMapping(value = "/downloadBpmn", method = RequestMethod.POST)
    @ApiOperation(value = "下载Bpmn文件", notes = "通过deploymentId 获取bpmn文件内容下载")
    public void downloadBpmn(@RequestParam @ApiParam(name = "deploymentId", value = "流程部署的id") String deploymentId,
                          @RequestParam @ApiParam(name = "name", value = "deployment的名字") String name,
                          HttpServletResponse response) throws IOException {
        InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, name);

        response.setHeader("content-disposition","attachment;fileName="+name);
        int count = 0;
        byte[] by = new byte[1024];
        //通过response对象获取OutputStream流
        OutputStream out = response.getOutputStream();
        while ((count = resourceAsStream.read(by)) != -1) {
            out.write(by, 0, count);//将缓冲区的数据输出到浏览器
        }
        resourceAsStream.close();
        out.flush();
        out.close();
    }

    // 这个是获取 部署的流程activiti 数据里的 ACT_RE_DEPLOYMENT 信息很少的那张表
    @SysLog("获取流程定义")
    @RequestMapping(value = "/listDefinition", method = RequestMethod.GET)
    @ApiOperation(value = "获取流程定义(简)", notes = "获取流程定义简单版本")
    public R getAllDeployment() {
        List<QueryDeployment> listdp = new ArrayList<>();
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment dp : list) {
            QueryDeployment d = new QueryDeployment();
            d.setDeployid(dp.getId());
            d.setName(dp.getName());
            d.setKey(dp.getKey());
            d.setVersion(dp.getVersion());
            d.setDeploymentTime(dp.getDeploymentTime());
            listdp.add(d);
        }
        return R.success(listdp);
    }

    // 这个是获取流程部署的另外个表里的内容。
    @SysLog("获取流程定义署2")
    @RequestMapping(value = "/listProcessDefinition", method = RequestMethod.GET)
    @ApiOperation(value = "获取流程定义(详)", notes = "获取流程定义详细内容，version是最新的")
    public R getAllDeployment2() {
        List<QueryProcessDefinition> listPro = new ArrayList<>();

        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .list();
        for (ProcessDefinition pd : list) {
            QueryProcessDefinition pf = new QueryProcessDefinition();
            pf.setDeploymentId(pd.getDeploymentId());
            pf.setCategory(pd.getCategory());
            pf.setID(pd.getId());
            pf.setDescription(pd.getDescription());
            pf.setKey(pd.getKey());
            pf.setName(pd.getName());
            pf.setVersion(pd.getVersion());
            pf.setEngineVersion(pd.getEngineVersion());
            pf.setResourceName(pd.getResourceName());
            pf.setDiagramResourceName(pd.getDiagramResourceName());
            pf.setHasStartFormKey(pd.hasStartFormKey());
            pf.setHasGraphicalNotation(pd.hasGraphicalNotation());
            pf.setSuspended(pd.isSuspended());
            pf.setTenantId(pd.getTenantId());
            pf.setEngineVersion(pd.getEngineVersion());
            listPro.add(pf);
        }
        return R.success(listPro);
    }

    // 分页获取流程部署的另外个表里的内容
    @SysLog("分页获取流程定义署2")
    @RequestMapping(value = "/pageListProcessDefinition", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取流程定义(详)", notes = "分页获取流程定义详细内容，version是最新的")
    public R getAllDeployment2ByPage(@RequestParam @ApiParam(name = "page", value = "页码") int page,
                                     @RequestParam @ApiParam(name = "size", value = "每页数量") int size) {
        List<QueryProcessDefinition> listPro = new ArrayList<>();

        if (page <= 1) {
            page = 0;
        } else {
            page = (page - 1) * size;
        }


        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().listPage(page, size);
        for (ProcessDefinition pd : list) {
            QueryProcessDefinition pf = new QueryProcessDefinition();
            pf.setDeploymentId(pd.getDeploymentId());
            pf.setCategory(pd.getCategory());
            pf.setID(pd.getId());
            pf.setDescription(pd.getDescription());
            pf.setKey(pd.getKey());
            pf.setName(pd.getName());
            pf.setVersion(pd.getVersion());
            pf.setEngineVersion(pd.getEngineVersion());
            pf.setResourceName(pd.getResourceName());
            pf.setDiagramResourceName(pd.getDiagramResourceName());
            pf.setHasStartFormKey(pd.hasStartFormKey());
            pf.setHasGraphicalNotation(pd.hasGraphicalNotation());
            pf.setSuspended(pd.isSuspended());
            pf.setTenantId(pd.getTenantId());
            pf.setEngineVersion(pd.getEngineVersion());
            listPro.add(pf);
        }

        List<ProcessDefinition> list1 = repositoryService.createProcessDefinitionQuery().list();

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("pagelist", listPro);
        resMap.put("total", list1.size());
        return R.success(resMap);
    }


    // 上传bpmn通过文件
    @SysLog("上传BPMN 文件")
    @RequestMapping(value = "/uploadBpmnByStream", method = RequestMethod.POST)
    @ApiOperation(value = "通过文件上传BPMN图", notes = "上传BPMN文件")
    // , @RequestParam @ApiParam(name = "name",value = "ACT_RE_DEPLOYMENT里的name")String name 这里暂时不需要给bpmn 其名字，因为绘图的时候会起名字 容易混淆
    public R uploadBPMN(@RequestParam @ApiParam(name = "bpmnFile", value = "需要上传的BPMN文件") MultipartFile bpmnFile
    ) throws Exception {
        // 获取文件名
        String fileName = bpmnFile.getOriginalFilename();
        // 获取拓展名
        String extName = FilenameUtils.getExtension(fileName);
        // 获取文件流
        InputStream fileInputStream = bpmnFile.getInputStream();
        Deployment deployment = null;
        if (extName.equals("zip")) {
            // 说明是zip文件
            ZipInputStream zip = new ZipInputStream(fileInputStream);
            deployment = repositoryService.createDeployment()//初始化流程
                    .addZipInputStream(zip)
                    .deploy();
        } else {
            deployment = repositoryService.createDeployment()//初始化流程
                    .addInputStream(fileName, fileInputStream)
                    .deploy();
        }
        return R.success(deployment.getId());
    }

    // 通过xml 字符串上传
    @SysLog("使用xml字符串上传BPMN 文件")
    @RequestMapping(value = "/uploadBpmnByStr", method = RequestMethod.POST)
    @ApiOperation(value = "通过XML字符串上传BPMN图", notes = "上传BPMN文件")
    //@RequestParam @ApiParam(name = "name",value = "ACT_RE_DEPLOYMENT里的name")String name
    public R uploadByXmlStr(@RequestBody @ApiParam(name = "bpm", value = "bpm对象里面是字符串") Bpm bpm
    ) {
        Deployment deployment = repositoryService.createDeployment().addString("CreateBPMNJSByXmlString.bpmn", bpm.getXmlStr())
                .deploy();
        return R.success();
    }

    // 获取xml文件
    @SysLog("获取BPMN的xml文件")
    @RequestMapping(value = "/getBpmnXml", method = RequestMethod.GET)
    @ApiOperation(value = "获取bpml的XML文件", notes = "获取xml")
    public void getBpmnXml(@RequestParam() @ApiParam(name = "resourceName", value = "上传时候的文件名") String resourceName,
                           @RequestParam() @ApiParam(name = "deploymentId", value = "流程") String deploymentId,
                           HttpServletResponse response) throws IOException {
        System.out.println(deploymentId);
        InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
        int count = inputStream.available();
        byte[] bytes = new byte[count];
        response.setContentType("text/xml");
        OutputStream outputStream = response.getOutputStream();
        while (inputStream.read(bytes) != -1) {
            outputStream.write(bytes);
        }
        inputStream.close();

    }

    // 删除流程定义
    @SysLog("删除流程定义")
    @RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除流程定义", notes = "删除流程定义")
    public R delDeployedById(@PathVariable("id") String id) {

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().deploymentId(id).singleResult();
        if (processInstance !=null){
            return R.fail("删除失败,请先结束已经开启的流程实例");
        }


        repositoryService.deleteDeployment(id, false);
        return R.success().setMsg("删除成功");
    }

    // 转化Depolyment
    private QueryDeployment convertDepoly(Deployment dp) {
        QueryDeployment qdp = new QueryDeployment();
        qdp.setCategory(dp.getCategory());
        qdp.setDeploymentTime(dp.getDeploymentTime());
        qdp.setTenantId(dp.getTenantId());
        qdp.setDeployid(dp.getId());
        qdp.setKey(dp.getKey());
        qdp.setVersion(dp.getVersion());
        qdp.setName(dp.getName());
        qdp.setProjectReleaseVersion(dp.getProjectReleaseVersion());
        return qdp;
    }
}
