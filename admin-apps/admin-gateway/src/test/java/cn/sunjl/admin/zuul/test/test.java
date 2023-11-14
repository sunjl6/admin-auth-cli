package cn.sunjl.admin.zuul.test;

import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.entity.auth.Resource;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.common.constant.CacheKey;
import cn.sunjl.admin.zuul.api.ResourceApi;
import com.alibaba.fastjson.JSON;
import net.oschina.j2cache.CacheChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class test {

    @Autowired
    private ResourceApi resourceApi;
    @Autowired
    private CacheChannel cacheChannel;
    @Test
    public void test(){
//        ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();
//        resourceQueryDTO.setUserId(3L);
//        R<List<Resource>> visible = resourceApi.visible(resourceQueryDTO);
////        R r = resourceApi.visible2(resourceQueryDTO);
//        System.out.println(visible);
        List<String> userPermmsionsList = (List<String>) cacheChannel.get(CacheKey.USER_RESOURCE, "3").getValue();
        String userId= "3";
        if (userPermmsionsList == null){
            ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();
            resourceQueryDTO.setUserId(new Long(userId));
            List<Resource> data = resourceApi.visible(resourceQueryDTO).getData();
            System.out.println("data==============="+data);
            if (data !=null && data.size()>0){
                data.stream().map((resource -> {
                    return resource.getMethod()+resource.getUrl();
                })).collect(Collectors.toList());
                cacheChannel.set(CacheKey.USER_RESOURCE,userId,data);
            }
        }
    }

}
