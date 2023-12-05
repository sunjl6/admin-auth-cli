package cn.sunjl.admin.authority.controller.core;
import java.util.List;

import cn.sunjl.admin.authority.biz.service.core.StationService;
import cn.sunjl.admin.authority.dto.core.StationPageDTO;
import cn.sunjl.admin.authority.dto.core.StationSaveDTO;
import cn.sunjl.admin.authority.dto.core.StationUpdateDTO;
import cn.sunjl.admin.authority.entity.core.Org;
import cn.sunjl.admin.authority.entity.core.Station;
import cn.sunjl.admin.base.BaseController;
import cn.sunjl.admin.base.R;
import cn.sunjl.admin.base.entity.SuperEntity;
import cn.sunjl.admin.database.mybatis.conditions.Wraps;
import cn.sunjl.admin.database.mybatis.conditions.query.LbqWrapper;
import cn.sunjl.admin.dozer.DozerUtils;
import cn.sunjl.admin.log.annotation.SysLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 前端控制器
 * 岗位
 */
@Slf4j
@RestController
@RequestMapping("/station")
@Api(value = "Station", tags = "岗位")
public class StationController extends BaseController {
    @Autowired
    private StationService stationService;
    @Autowired
    private DozerUtils dozer;
    /**
     * 分页查询岗位
     */
    @ApiOperation(value = "分页查询岗位", notes = "分页查询岗位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    @SysLog("分页查询岗位")
    public R<IPage<Station>> page(StationPageDTO data) {
        Page<Station> page = getPage();
//        stationService.findStationPage(page, data);
        LbqWrapper<Station> query = Wraps.lbQ(dozer.map(data,Station.class));
        stationService.page(page,query);
        return success(page);
    }
    //    查询所有岗位不分页
    @ApiOperation(value = "查询所有岗位不分页", notes = "查询所有岗位不分页")
    @GetMapping("/getAllStations")
    @SysLog("查询所有岗位不分页")
    public R<List<Station>> getAllStations() {
        return success(stationService.list());
    }

    /**
     * 根据id查询岗位
     */
    @ApiOperation(value = "查询岗位", notes = "查询岗位")
    @GetMapping("/{id}")
    @SysLog("查询岗位")
    public R<Station> get(@PathVariable Long id) {
        return success(stationService.getById(id));
    }

    /**
     * 新增岗位
     */
    @ApiOperation(value = "新增岗位", notes = "新增岗位不为空的字段")
    @PostMapping
    @SysLog("新增岗位")
    public R<Station> save(@RequestBody @Validated StationSaveDTO data) {
        Station station = dozer.map(data, Station.class);
        stationService.save(station);
        return success(station);
    }
//    更具部门id 查询下面所有岗位
    @ApiOperation(value = "根据部门ID查询所有岗位", notes = "根据部门ID查询所有岗位")
    @GetMapping("/getStationsByOrgId/{orgId}")
    @SysLog("根据部门ID查询所有岗位")
    public R<List<Station>> getStationsByOrgId(@PathVariable Long orgId) {
        QueryWrapper<Station> wrapper = new QueryWrapper<>();
        wrapper.eq("org_id",orgId);
        return success(stationService.list(wrapper));
    }
    /**
     * 修改岗位
     */
    @ApiOperation(value = "修改岗位", notes = "修改岗位不为空的字段")
    @PutMapping
    @SysLog("修改岗位")
    public R<Station> update(@RequestBody @Validated(SuperEntity.Update.class) StationUpdateDTO data) {
        Station station = dozer.map(data, Station.class);
        stationService.updateById(station);
        return success(station);
    }

    /**
     * 删除岗位
     */
    @ApiOperation(value = "删除岗位", notes = "根据id物理删除岗位")
    @SysLog("删除岗位")
    @DeleteMapping
    public R<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {
        stationService.removeByIds(ids);
        return success();
    }
}