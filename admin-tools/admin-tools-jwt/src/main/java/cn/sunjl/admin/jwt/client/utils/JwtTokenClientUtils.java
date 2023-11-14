package cn.sunjl.admin.jwt.client.utils;

import cn.sunjl.admin.exception.BizException;
import cn.sunjl.admin.jwt.client.properties.AuthClientProperties;
import cn.sunjl.admin.jwt.utils.JwtHelper;
import cn.sunjl.admin.jwt.utils.JwtUserInfo;
import lombok.AllArgsConstructor;

/**
 * JwtToken 客户端工具
 *
 */
@AllArgsConstructor
public class JwtTokenClientUtils {
    /**
     * 用于 认证服务的 客户端使用（如 网关） ， 在网关获取到token后，
     * 调用此工具类进行token 解析。
     * 客户端一般只需要解析token 即可
     */
    private AuthClientProperties authClientProperties;

    /**
     * 解析token
     *
     * @param token
     * @return
     * @throws BizException
     */
    public JwtUserInfo getUserInfo(String token) throws BizException {
        AuthClientProperties.TokenInfo userTokenInfo = authClientProperties.getUser();
        return JwtHelper.getJwtFromToken(token, userTokenInfo.getPubKey());
    }
}
