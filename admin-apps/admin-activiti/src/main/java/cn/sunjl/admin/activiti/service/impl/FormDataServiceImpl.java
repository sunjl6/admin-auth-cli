package cn.sunjl.admin.activiti.service.impl;

import cn.sunjl.admin.activiti.dao.FormDataMapper;
import cn.sunjl.admin.activiti.pojo.FormData;
import cn.sunjl.admin.activiti.service.FormDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FormDataServiceImpl extends ServiceImpl<FormDataMapper, FormData> implements FormDataService {
}
