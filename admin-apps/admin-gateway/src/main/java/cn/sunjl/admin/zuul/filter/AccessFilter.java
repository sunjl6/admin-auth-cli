package cn.sunjl.admin.zuul.filter;

import cn.hutool.core.util.StrUtil;
import cn.sunjl.admin.authority.dto.auth.ResourceQueryDTO;
import cn.sunjl.admin.authority.entity.auth.Resource;
import cn.sunjl.admin.common.constant.CacheKey;
import cn.sunjl.admin.context.BaseContextConstants;
import cn.sunjl.admin.exception.code.ExceptionCode;
import cn.sunjl.admin.zuul.api.ResourceApi;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
@Component
public class AccessFilter extends BaseFilter{
    @Autowired
    private ResourceApi resourceApi;
    @Autowired
    private CacheChannel cacheChannel;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER+10;

    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if(!ctx.sendZuulResponse()){
            return false;
        }
        return true;
    }
    /**
     * 验证当前用户是否拥有某个URI的访问权限
     */
    @Override
    public Object run() throws ZuulException {
      //  第1步：判断当前请求uri是否需要忽略
        if (isIgnoreToken()){
            return null;
        }
      //  第2步：获取当前请求的请求方式和uri，拼接成GET/user/page这种形式，称为权限标识符
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String url =StrUtil.subSuf(requestURI,zuulPrefix.length());
        url = StrUtil.subSuf(url,url.indexOf("/",1));
        String permission = method+url;
//        requestUrl++++/api/authority/user/profile
//        切割后的Url++++/authority/user/profile
//        再次切割后的Url++++/user/profile

     //  第3步：从缓存中获取所有需要进行鉴权的资源(同样是由资源表的method字段值+url字段值拼接成)，如果没有获取到则通过Feign调用权限服务获取并放入缓存中
        CacheObject cacheObject = cacheChannel.get(CacheKey.RESOURCE, CacheKey.RESOURCE_NEED_TO_CHECK);
        List<String> list = (List<String>) cacheObject.getValue();
        if (list == null){
            list = resourceApi.list().getData();
            if (list != null && list.size()>0){
                cacheChannel.set(CacheKey.RESOURCE,CacheKey.RESOURCE_NEED_TO_CHECK,list);
            }

        }

        //  第4步：判断这些资源是否包含当前请求的权限标识符，如果不包含当前请求的权限标识符，则返回未经授权错误提示
        if(list != null){

            long count = list.stream().filter((r) -> {
                return permission.startsWith(r);
            }).count();
            if (count == 0){
                errorResponse(ExceptionCode.UNAUTHORIZED.getMsg(), ExceptionCode.UNAUTHORIZED.getCode(), 200);
                return null;
            }
        }

        //  第5步：如果包含当前的权限标识符，则从zuul header中取出用户id，根据用户id取出缓存中的用户拥有的权限，如果没有取到则通过Feign调用权限服务获取并放入缓存，判断用户拥有的权限是否包含当前请求的权限标识符
        String userId = RequestContext.getCurrentContext().getZuulRequestHeaders().get(BaseContextConstants.JWT_KEY_USER_ID);

//        if (userId == null){
//            return  R.fail("空token");
//        }

        List<String> userPermmsionsList = new ArrayList<String>();
        CacheObject userPermissionObject = cacheChannel.get(CacheKey.USER_RESOURCE, userId);
         userPermmsionsList  = (List<String>) userPermissionObject.getValue();
        if (userPermmsionsList == null){
            ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();

            resourceQueryDTO.setUserId(new Long(userId));
            List<Resource> data = resourceApi.visible(resourceQueryDTO).getData();
            //            List<String> resourceList = list.stream().map((Resource r) -> {
//                return r.getMethod() + r.getUrl();
//            }).collect(Collectors.toList());

//            if (data !=null && data.size()>0){
//                List<String> resourceList = data.stream().map((Resource r)->{
//                    return r.getName()+r.getUrl();
//                }).collect(Collectors.toList());
//                cacheChannel.set(CacheKey.USER_RESOURCE,userId,resourceList);
//            }
        }

        Long count2 = 0L;
        //  第6步：如果用户拥有的权限包含当前请求的权限标识符则说明当前用户拥有权限，直接放行
        count2 =  userPermmsionsList.stream().filter((resource)->{
            return permission.contains(resource);
        }).count();
//        System.out.println("拥有的权限列表"+userPermissionObject);
//        System.out.println("访问的权限"+permission);
//        System.out.println("拥有权限的数量"+count2);
      //  第7步：如果用户拥有的权限不包含当前请求的权限标识符则说明当前用户没有权限，返回未经授权错误提示
        if (count2>0){
            return null;
        }else{
            errorResponse(ExceptionCode.UNAUTHORIZED.getMsg(), ExceptionCode.UNAUTHORIZED.getCode(), 200);
            return null;
        }

    }
}
