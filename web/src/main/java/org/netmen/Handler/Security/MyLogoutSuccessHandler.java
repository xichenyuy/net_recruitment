package org.netmen.Handler.Security;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.HashMap;

public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        HashMap result = new HashMap();
        result.put("code", 0);
        result.put("message", "注销成功");

        //将结果转化为json字符串
        String json = JSON.toJSONString(result);

        //返回json给前端
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
    }
}
