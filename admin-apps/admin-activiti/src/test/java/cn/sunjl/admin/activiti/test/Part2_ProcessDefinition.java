package cn.sunjl.admin.activiti.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Part2_ProcessDefinition {

    @Autowired
    private RepositoryService repositoryService;
    // 这里操作的是 ACT_RE_PROCDEF
    //查询流程定义 返回结果是list
    @Test
    public void getDefinitions(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .list();
        for(ProcessDefinition pd : list){
            System.out.println("------流程定义--------");
            System.out.println("Name："+pd.getName());
            System.out.println("Key："+pd.getKey());
            System.out.println("ResourceName："+pd.getResourceName());
            System.out.println("DeploymentId："+pd.getDeploymentId());
            System.out.println("Version："+pd.getVersion());
            System.out.println("id："+pd.getId()); // id 是 key + deployment_id

        }

    }

    //删除流程定义
    @Test
    public void delDefinition(){

        String pdID="09ec0f5a-999b-11ee-894e-f426796d2a71";
        repositoryService.deleteDeployment(pdID,true);
        System.out.println("删除流程定义成功");
    }
}
