package cn.sunjl.admin.authority;

import cn.sunjl.admin.authority.utils.MinioUtil;

import io.minio.messages.Bucket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class test {
    @Autowired
    private MinioUtil minioUtil;
    @Test
    public void test1(){
//        Boolean imgs = minioUtil.bucketExists("imgs");
//        System.out.println(imgs);
//        minioUtil.makeBucket("test01");
//        minioUtil.removeBucket("test01");
//        List<Bucket> allBuckets = minioUtil.getAllBuckets();
//        System.out.println(allBuckets.get(0).name());


    }

}
