package cn.sunjl.admin.authority.biz.dao.auth;

import java.util.List;

import cn.sunjl.admin.authority.entity.auth.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 菜单
 * </p>
 *
 */
@Repository
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 查询用户可用菜单
     *
     * @param group
     * @param userId
     * @return
     */
    List<Menu> findVisibleMenu(@Param("group") String group, @Param("userId") Long userId);
}
