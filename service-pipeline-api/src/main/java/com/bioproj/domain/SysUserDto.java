package com.bioproj.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "SysUserDto", description = "系统用户信息")
public class SysUserDto implements Serializable {

    @ApiModelProperty("用户id")
    private Integer id;

    private Integer tenantId;

    /*
    用户真实名称
     */
    @ApiModelProperty("用户名称")
    private String name;

    /*
    登录名
     */
    @ApiModelProperty("用户登录名")
    private String loginName;

    /*
    手机号
     */
    @ApiModelProperty("用户手机号")
    private String phoneNumber;


    /*
    用户邮箱
     */
    @ApiModelProperty("用户邮箱")
    private String email;

    /*
    性别
     */
    @ApiModelProperty("用户性别")
    private Integer gender;

    /*
    微信openId
     */
    @ApiModelProperty("微信公众号 openId")
    private String openId;

    @ApiModelProperty("微信小程序 openId")
    private String miniProgramOpenId;

    /*
    微信昵称
     */
    @ApiModelProperty("用户昵称")
    private String nickName;


    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;



    /*
    用户token，用于服务之间调用传递
     */
    private String token;

    private Boolean donor;

}
