package com.bin.onetextcode.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public enum ErrorCode {

    SUCESS(0,"OK"),
    PARAM_ERROR(10000,"参数错误"),
    NOT_LOGIN_ERROR(40001,"未登录"),
    NOT_AUTH_ERROR(40002,"无权限"),
    NOT_FOUND_ERROR(40400,"请求数据访问不存在"),
    FORBIDDEN_ERROR(40300,"禁止访问"),
    SYSTEM_ERROR(50000,"系统错误"),
    OPERATION_ERROR(50001,"操作失败");



    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(int code,String message){
        this.code = code;
        this.message = message;
    }

}
