package cn.sunjl.admin.authority.controller.fileUpload;


import cn.sunjl.admin.authority.biz.service.auth.UserService;
import cn.sunjl.admin.authority.config.MinioConfig;
import cn.sunjl.admin.authority.entity.auth.User;
import cn.sunjl.admin.authority.utils.MinioUtil;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import io.minio.messages.Bucket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "file", tags = "文件上传接口")
@Slf4j
@RestController
@RequestMapping(value = "product/file")
public class FileController extends BaseController {


    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "查看存储bucket是否存在")
    @GetMapping("/bucketExists")
    public R bucketExists(@RequestParam("bucketName") String bucketName) {
        return R.success().put("bucketName",minioUtil.bucketExists(bucketName));
    }

    @ApiOperation(value = "创建存储bucket")
    @GetMapping("/makeBucket")
    public R makeBucket(String bucketName) {
        return R.success().put("bucketName",minioUtil.makeBucket(bucketName));
    }

    @ApiOperation(value = "删除存储bucket")
    @GetMapping("/removeBucket")
    public R removeBucket(String bucketName) {
        return R.success().put("bucketName",minioUtil.removeBucket(bucketName));
    }

    @ApiOperation(value = "获取全部bucket")
    @GetMapping("/getAllBuckets")
    public R getAllBuckets() {
        List<Bucket> allBuckets = minioUtil.getAllBuckets();
        return R.success().put("allBuckets",allBuckets);
    }

    @ApiOperation(value = "文件上传返回url")
    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file) {
        String objectName = minioUtil.upload(file);
        if (null != objectName) {
            return R.success((minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + objectName));
        }
        return R.fail("上传失败");
    }

    @ApiOperation(value = "图片/视频预览")
    @GetMapping("/preview")
    public R preview(@RequestParam("fileName") String fileName) {
        return R.success().put("filleName",minioUtil.preview(fileName));
    }

    @ApiOperation(value = "文件下载")
    @GetMapping("/download")
    public R download(@RequestParam("fileName") String fileName, HttpServletResponse res) {
        minioUtil.download(fileName,res);
        return R.success();
    }

    @ApiOperation(value = "删除文件", notes = "根据url地址删除文件")
    @PostMapping("/delete")
    public R remove(String url) {
        System.out.println(url);
        if (url == null){
            return R.fail("用未上传头像，请重新上传");
        }
        String objName = url.substring(url.lastIndexOf(minioConfig.getBucketName()+"/") + minioConfig.getBucketName().length()+1);
        System.out.println(objName);
        minioUtil.remove(objName);
        return R.success().put("objName",objName);
    }

}

