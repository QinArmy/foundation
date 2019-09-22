package org.qinarmy.foundation.core;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * created  on 2019-03-17.
 */
public final class Result {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private ResultCode code;

    private String msg;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private Object data;

    private Class<?> view;

    /**
     * 记录服务器时间,客户端可用于计算网络延迟
     */
    @JsonFormat(pattern = "uuuu-MM-dd HH:mm:ss.SSS")
    private LocalDateTime serverTime;


    public Result() {
    }


    public ResultCode getCode() {
        if(code == null){
            code = ResultCode.OK;
        }
        return code;
    }

    public Result setCode(ResultCode code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        if(msg == null){
            msg = getCode().display();
        }
        return msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public LocalDateTime getServerTime() {
        if(serverTime == null){
            serverTime = LocalDateTime.now();
        }
        return serverTime;
    }

    public Class<?> getView() {
        return view;
    }

    public Result setView(Class<?> view) {
        this.view = view;
        return this;
    }
}
