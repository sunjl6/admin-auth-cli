package cn.sunjl.admin.authority.biz.service.auth;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ValidateCodeService {
    void create(String key, HttpServletResponse response) throws IOException;

    Boolean checkCode(String key, String code);
}
