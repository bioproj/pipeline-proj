package com.bioproj.common;

import com.bioproj.domain.SysUserDto;

public class SysUserInfoContext {

    private static ThreadLocal<SysUserDto> sysUserInfo = new ThreadLocal<>();

    public static SysUserDto getUser() {
        return sysUserInfo.get();
    }

    public static void setUser(SysUserDto user) {
        sysUserInfo.set(user);
    }

    public static void remove(){
        sysUserInfo.remove();
    }

}
