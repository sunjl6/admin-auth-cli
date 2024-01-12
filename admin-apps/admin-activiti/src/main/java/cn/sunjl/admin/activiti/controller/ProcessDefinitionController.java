package cn.sunjl.admin.activiti.controller;

import cn.sunjl.admin.activiti.dto.Bpm;
import cn.sunjl.admin.activiti.entity.QueryDeployment;
import cn.sunjl.admin.activiti.entity.QueryProcessDefinition;
import cn.sunjl.admin.activiti.entity.UserTaskModel;
import cn.sunjl.admin.activiti.pojo.ActGeByteArrary;
import cn.sunjl.admin.activiti.service.ActGeByteArraryService;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.log.annotation.SysLog;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    @Autowired
    private ActGeByteArraryService actGeByteArraryService;
    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;


    //根据 process Definition id 清除缓存
    protected void  cleanDefinitionCache(String deploymenetId){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymenetId).singleResult();
        ((ProcessEngineConfigurationImpl) processEngineConfiguration).getProcessDefinitionCache().remove(processDefinition.getId());
    }

    // 修改流程节点执行星系
    @SysLog("修改流程节点执行人信息")
    @Transactional
    @RequestMapping(value = "/modifyProcessNodeByDeploymentId", method = RequestMethod.POST)
    @ApiOperation(value = "修改节点执行人信息", notes = "通过deploymentId 获取bpmn文件内容下的节点执行人的信息")
    public R modifyNode(@RequestParam @ApiParam(name = "deploymentId",value = "流程部署的id")String deploymentId,
                        @RequestBody @ApiParam(name = "list",value = "执行人列表")List<UserTaskModel> list ) throws DocumentException, IOException {
        byte[] byteArray = this.getByteArray(deploymentId);
        SAXReader saxReader = new SAXReader();
        saxReader.setEncoding("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        // 获取所有用户节点
        Document document = saxReader.read(bis);
        Element rootElement = document.getRootElement();
        Element process = rootElement.element("process");
        List<Element> userTaskNodeList = process.elements("userTask");

        // 遍历对应节点id，修改节点属性
        for (Element element : userTaskNodeList) {
            // 拿到 userTask 下面的 遍历的每个元素的id 的值
            String id = element.attributeValue("id");
            // 从提交的用户模型数据集合里找出和当前id一样的记录，
            UserTaskModel userTaskModel =
                    list.stream().filter(u -> u.getId().equals(id)).collect(Collectors.toList()).get(0);
            // 把匹配到的结果对象的 type 类型取出来
            String type = userTaskModel.getType();
            // 取出bpmn里当前节点的 执行人
            Attribute assignee = element.attribute("assignee");
            // 判断如歌执行人不是空
            if (assignee != null) {
                // 那么就先删除执行人
                element.remove(assignee);
            }
            // 获取当前bpmn节点里的 候选人
            Attribute candidateUsers = element.attribute("candidateUsers");
            // 判断如果空，
            if (candidateUsers != null) {
                // 那么就先删除内容
                element.remove(candidateUsers);
            }
            Attribute candidateGroups = element.attribute("candidateGroups");
            if (candidateGroups != null) {
                element.remove(candidateGroups);
            }
            // 判断 type 类型
            switch (type) {
                case "0":
                    break;
                case "1":
                    element.addAttribute("activiti:assignee", userTaskModel.getAssignee());
                    break;
                case "2":
                    // 这里要把数组合成成字符串 例如 zhangsan,lisi
                    String candidateUserStr ="";
                    List<String> candidateUsersList = userTaskModel.getCandidateUsers();

                    for (String s : candidateUsersList) {
                       if (candidateUsersList.indexOf(s) == candidateUsersList.size()-1){
                           candidateUserStr = candidateUserStr + s;
                       }else{
                           candidateUserStr = candidateUserStr + s+ ',';
                       }
                    }

                    element.addAttribute("activiti:candidateUsers",candidateUserStr );
                    break;
                case "3":
                    element.addAttribute("activiti:candidateGroups", userTaskModel.getCandidateGroups());
                    break;
            }
        }
        // 将修改后的流程图文件转为字节数组
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLWriter xmlWriter = new XMLWriter(out);
        xmlWriter.write(document);
        xmlWriter.flush();
        final byte[] bytes = out.toByteArray();



        // 将字节数组写回数据库
        QueryWrapper<ActGeByteArrary> wrapper = new QueryWrapper<>();
        wrapper.eq("DEPLOYMENT_ID_",deploymentId);
        ActGeByteArrary actone = actGeByteArraryService.getOne(wrapper);
        actone.setBytes(bytes);
        actGeByteArraryService.updateById(actone);
//        LobHandler lobHandler = new DefaultLobHandler();
//        String sql = "update ACT_GE_BYTEARRAY set BYTES_=? where NAME_ LIKE '%bpmn20.xml' and " +
//                "DEPLOYMENT_ID_= ? ";
//        jdbcTemplate.execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
//            @Override
//            protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException, DataAccessException {
//                lobCreator.setBlobAsBytes(ps, 1, bytes);
//                ps.setString(2, deploymentId);
//            }
//        });
        /**流程定义发布后，若你使用了该流程，其就会在缓存中缓存了流程定义的解析后的对象，供整个引擎使用，
         * 这时你更改了流程定义的XML后，那份缓存并没有实现了更改，因此，需要告诉引擎，让他清空缓存中的该流程定义
         */
//        ((ProcessEngineConfigurationImpl)processEngineConfiguration).getProcessDefinitionCache().remove(actone.getDeploymentId());

        //治理传递的id 是 ACT_RE_PROCDEF 里的id 不是 deploymentId
//        ((ProcessEngineConfigurationImpl) processEngineConfiguration).getProcessDefinitionCache().remove(actone.getName());
        cleanDefinitionCache(deploymentId);
        return R.success();
    }

    // 通过deploymentId 获取这个流程的所有节点信息
    @SysLog("获取流程节点执行人信息")
    @RequestMapping(value = "/getProcessNodeByDeploymentId", method = RequestMethod.GET)
    @ApiOperation(value = "获取节点执行人信息", notes = "通过deploymentId 获取bpmn文件内容下的节点执行人的信息")
    public R getProcessNodeByDeploymentId(@RequestParam @ApiParam(name = "deploymentId",value = "流程部署的id")String deploymentId) throws IOException, DocumentException {
        byte[] byteArray = this.getByteArray(deploymentId);
        SAXReader saxReader = new SAXReader();
//         获取流程图文件中的userTask节点的所有属性
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        Document document = saxReader.read(bis);
        Element rootElement = document.getRootElement();
        Element process = rootElement.element("process");
        List<Element> userTaskList = process.elements("userTask");

        ArrayList<UserTaskModel> list = Lists.newArrayList();

        // 包装成适合前端展示的集合并返回
        for (Element element : userTaskList) {
            UserTaskModel userTaskModel = new UserTaskModel();
            userTaskModel.setId(element.attributeValue("id"));
            userTaskModel.setName(element.attributeValue("name"));

            String type = "0";
            String assignee = element.attributeValue("assignee");
            String candidateUsers = element.attributeValue("candidateUsers");
            String candidateGroups = element.attributeValue("candidateGroups");
            if (StringUtils.isNotEmpty(candidateGroups)) {
                type = "3";
                userTaskModel.setCandidateGroups(candidateGroups);
            }
            if (StringUtils.isNotEmpty(candidateUsers)) {
                type = "2";
                String[] splitCandidateUsers = candidateUsers.split(",");
                List<String> arr = new ArrayList<>();
                for (String s : splitCandidateUsers) {
                    arr.add(s);
                }

                userTaskModel.setCandidateUsers(arr);
            }
            if (StringUtils.isNotEmpty(assignee)) {
                type = "1";
                userTaskModel.setAssignee(assignee);
            }
            userTaskModel.setType(type);
            list.add(userTaskModel);
        }
        bis.close();
        return R.success(list);
    }
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

    // 获取bpmn的字节文件
    public byte[] getByteArray(String deploymentId){
        // 从ACT_GE_BYTEARRAY表中获取流程图的二进制文件
        // 此操作对象必须是已部署的模型，此时流程定义的二进制文件才是以bpmn20.xml结尾的。
        QueryWrapper<ActGeByteArrary> wrapper = new QueryWrapper<>();
        wrapper.eq("DEPLOYMENT_ID_", deploymentId);
        ActGeByteArrary byId = actGeByteArraryService.getOne(wrapper);
        byte[] bytes = byId.getBytes();
        return bytes;
    }

    /**
     * 根据流程id清除activiti引擎中流程缓存
     * @param processId
     */


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
