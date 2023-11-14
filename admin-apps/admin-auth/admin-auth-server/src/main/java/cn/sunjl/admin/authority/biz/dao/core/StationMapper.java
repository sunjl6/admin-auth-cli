package cn.sunjl.admin.authority.biz.dao.core;

import cn.sunjl.admin.authority.entity.core.Station;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

@Repository
public interface StationMapper extends BaseMapper<Station> {
    IPage<Station> findStationPage(Page page, @Param(Constants.WRAPPER) Wrapper<Station> queryWrapper);
}
