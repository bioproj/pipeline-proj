package com.bioproj.filter;


import com.bioproj.common.SysUserInfoContext;
import com.bioproj.domain.SysUserDto;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PipelineInterceptor implements HandlerInterceptor {

    Boolean authDebug;

    public PipelineInterceptor(Boolean authDebug) {
        this.authDebug = authDebug;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if( authDebug){
            SysUserDto user = new SysUserDto();//SysUserInfoContext.getUser();
//            user = new ysUserDto();
            user.setId(1);
            user.setLoginName("admin");
            SysUserInfoContext.setUser(user);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
