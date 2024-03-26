package com.bioproj.filter;

import com.mbiolance.cloud.auth.common.SysUserInfoContext;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
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

        SysUserDto user = SysUserInfoContext.getUser();
        if(user==null && authDebug){
            user = new SysUserDto();
            user.setId(1);
            user.setLoginName("admin");
            SysUserInfoContext.setUser(user);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
