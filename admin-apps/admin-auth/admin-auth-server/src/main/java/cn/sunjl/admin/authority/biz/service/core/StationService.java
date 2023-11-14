package cn.sunjl.admin.authority.biz.service.core;

import cn.sunjl.admin.authority.dto.core.StationPageDTO;
import cn.sunjl.admin.authority.entity.core.Station;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface StationService extends IService<Station> {
    /**
     * 分页
     */
    IPage<Station> findStationPage(Page page, StationPageDTO data);
}
