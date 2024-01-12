package cn.sunjl.admin.activiti.test;

import cn.sunjl.admin.activiti.entity.UserTaskModel;
import cn.sunjl.admin.activiti.pojo.ActGeByteArrary;
import cn.sunjl.admin.activiti.service.ActGeByteArraryService;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.event.BaseEntityEventListener;
import org.activiti.engine.repository.Deployment;
import org.apache.commons.compress.utils.Lists;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Part1_Deployment  {
    // 这里操作的是 ACT_RE_DEPLOYMENT
    @Autowired
    private RepositoryService repositoryService;
    // 通过bpmn 部署流程
    @Autowired
    private ActGeByteArraryService actGeByteArraryService;
    public byte[] getByteArray(String deploymentId){
        // 从ACT_GE_BYTEARRAY表中获取流程图的二进制文件
        // 此操作对象必须是已部署的模型，此时流程定义的二进制文件才是以bpmn20.xml结尾的。
        QueryWrapper<ActGeByteArrary> wrapper = new QueryWrapper<>();
        wrapper.eq("DEPLOYMENT_ID_", deploymentId);
        ActGeByteArrary byId = actGeByteArraryService.getOne(wrapper);
        byte[] bytes = byId.getBytes();
        return bytes;
    }
    @Test
    public void test222() throws DocumentException, IOException {
        String deploymentId = "58b0bea4-a9ef-11ee-bcdd-f426796d2a6d";
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
//                userTaskModel.setCandidateUsers(candidateUsers);
            }
            if (StringUtils.isNotEmpty(assignee)) {
                type = "1";
                userTaskModel.setAssignee(assignee);
            }
            userTaskModel.setType(type);
            list.add(userTaskModel);
        }
        bis.close();
        System.out.println(list);
    }
    @Test
    public void testDeployment(){
        String file = "BPMN/webjs.bpmn";
        Deployment deploy = repositoryService.createDeployment().addClasspathResource(file)
                .name("平行网关测试流程").deploy();
        System.out.println(deploy.getName());
    }
    // 通过字符串部署
    @Test
    public void testDeploymentStr(){
        String str = "";
        Deployment deploy = repositoryService.createDeployment().addString("StrBPMN.bpmn","<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:activiti=\"http://activiti.org/bpmn\" id=\"m1577635100724\" name=\"\" targetNamespace=\"http://www.activiti.org/testm1577635100724\">\n" +
                        "  <process id=\"process\" name=\"通过xml字符串上传的bpmn\" processType=\"None\" isClosed=\"false\" isExecutable=\"true\">\n" +
                        "    <extensionElements>\n" +
                        "      <camunda:properties>\n" +
                        "        <camunda:property name=\"a\" value=\"1\" />\n" +
                        "      </camunda:properties>\n" +
                        "    </extensionElements>\n" +
                        "    <startEvent id=\"_2\" name=\"start\">\n" +
                        "      <outgoing>Flow_030plg6</outgoing>\n" +
                        "    </startEvent>\n" +
                        "    <sequenceFlow id=\"Flow_030plg6\" sourceRef=\"_2\" targetRef=\"Activity_0s97bs8\" />\n" +
                        "    <userTask id=\"Activity_0s97bs8\" name=\"xml字符串申请请假\" activiti:assignee=\"sjl\">\n" +
                        "      <incoming>Flow_030plg6</incoming>\n" +
                        "    </userTask>\n" +
                        "  </process>\n" +
                        "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_leave\">\n" +
                        "    <bpmndi:BPMNPlane id=\"BPMNPlane_leave\" bpmnElement=\"process\">\n" +
                        "      <bpmndi:BPMNEdge id=\"Flow_030plg6_di\" bpmnElement=\"Flow_030plg6\">\n" +
                        "        <di:waypoint x=\"236\" y=\"250\" />\n" +
                        "        <di:waypoint x=\"290\" y=\"250\" />\n" +
                        "      </bpmndi:BPMNEdge>\n" +
                        "      <bpmndi:BPMNShape id=\"BPMNShape__2\" bpmnElement=\"_2\">\n" +
                        "        <omgdc:Bounds x=\"204\" y=\"234\" width=\"32\" height=\"32\" />\n" +
                        "        <bpmndi:BPMNLabel>\n" +
                        "          <omgdc:Bounds x=\"210\" y=\"266\" width=\"22\" height=\"14\" />\n" +
                        "        </bpmndi:BPMNLabel>\n" +
                        "      </bpmndi:BPMNShape>\n" +
                        "      <bpmndi:BPMNShape id=\"Activity_0zz60a7_di\" bpmnElement=\"Activity_0s97bs8\">\n" +
                        "        <omgdc:Bounds x=\"290\" y=\"210\" width=\"100\" height=\"80\" />\n" +
                        "      </bpmndi:BPMNShape>\n" +
                        "    </bpmndi:BPMNPlane>\n" +
                        "  </bpmndi:BPMNDiagram>\n" +
                        "</definitions>\n")
                .name("str部署BPMN").deploy();
        System.out.println(deploy.getName());
    }
    //通过zip格式部署流程
    @Test
    public void initDeploymentZIP() {
        InputStream fileInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("BPMN/part1_deployment.bpmn20.zip");
        ZipInputStream zip=new ZipInputStream(fileInputStream);
        Deployment deployment=repositoryService.createDeployment()
                .addZipInputStream(zip)
                .name("流程部署测试zip")
                .deploy();
        System.out.println(deployment.getName());
    }
    // 获取所有部署的流程 ，
    @Test
    public void getDeployments(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("id：" +list.get(i).getId());
            System.out.println("名字："+list.get(i).getName());
            System.out.println("部署时间"+list.get(i).getDeploymentTime());
            System.out.println(list.get(i).getCategory());
            System.out.println(list.get(i).getKey());
            System.out.println(list.get(i).getProjectReleaseVersion());
            System.out.println(list.get(i).getTenantId());
            System.out.println(list.get(i).getVersion());
        }
    }

}
