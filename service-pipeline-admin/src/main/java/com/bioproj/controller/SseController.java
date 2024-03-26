package com.bioproj.controller;

import com.bioproj.service.sse.ISseServer;
import com.bioproj.service.sse.SseServerImpl;
import com.mbiolance.cloud.auth.common.SysUserInfoContext;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@Slf4j
@Api(tags={"sse"})
public class SseController {

    /**
     * 用户SSE连接
     * 它返回一个SseEmitter实例，这时候连接就已经创建了.
     *
     * @return
     */
    @GetMapping("/userConnect")
    public SseEmitter connect() {
        /**
         * 一般取登录用户账号作为 messageId。分组的话需要约定 messageId的格式。
         * 这里模拟创建一个用户连接
         */
        SysUserDto user = SysUserInfoContext.getUser();

//        String userId = "userId-1";// + RandomUtils.nextInt(1, 10);
        return SseServerImpl.createConnect(user.getId()+"");
    }
    @GetMapping("/testUserConnect")
    public SseEmitter testUserConnect() {
        /**
         * 一般取登录用户账号作为 messageId。分组的话需要约定 messageId的格式。
         * 这里模拟创建一个用户连接
         */
        SysUserDto user = SysUserInfoContext.getUser();

//        String userId = "userId-1";// + RandomUtils.nextInt(1, 10);
        return SseServerImpl.createConnect("1");
    }
    /**
     * 模拟实例：下载进度条显示。 前端访问下载接口之前，先建立用户SSE连接，然后访问下载接口，服务端推送消息。
     * http://localhost:8080/sse/downLoad/userId-1
     *
     * @throws InterruptedException
     */
    @GetMapping("/downLoad/{userId}")
    public void pushOne(@PathVariable("userId") String userId) throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            if (i > 50 && i < 70) {
                Thread.sleep(500L);
            } else {
                Thread.sleep(100L);
            }
            System.out.println("sendMessage --> 消息=" + i);
            SseServerImpl.sendMessage(userId, String.valueOf(i));
        }
        System.out.println("下载成功");
    }


    /**
     * 广播发送。http://localhost:8080/sse/pushAllUser
     *
     * @throws InterruptedException
     */
    @GetMapping("/pushAllUser")
    public void pushAllUser() throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            if (i > 50 && i < 70) {
                Thread.sleep(500L);
            } else {
                Thread.sleep(100L);
            }
            System.out.println("batchAllSendMessage --> 消息=" + i);
            SseServerImpl.batchAllSendMessage(String.valueOf(i));
        }
    }



}
