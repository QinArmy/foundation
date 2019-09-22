package org.qinarmy.foundation.core;

import org.qinarmy.foundation.struct.CodeEnum;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * created  on 2019-03-13.
 */
public enum ResultCode implements CodeEnum {

    OK(200, "成功", null),

    CLIENT_ERROR(400,"客户端错误",null),
    WEB_PARAM_ERROR(430,"客户端参数未通过校验",CLIENT_ERROR),


    SERVER_ERROR(500,"服务端内部错误",null),

    METHOD_INVALID_PARAM(530,"方法入参无效",SERVER_ERROR),

    // 安全
    SECURITY_ERROR(1000,"安全类异常",null),
    KEY_ERROR(1001,"安全密钥异常",SECURITY_ERROR),


    // 数据访问
    DATA_ACCESS(2000,"数据访问出错",null),
    NO_RESULT(2001,"没有查到数据", DATA_ACCESS),
    NO_UNIQUE(2002,"无唯一结果", DATA_ACCESS),
    OPTIMISTIC_LOCKING(2003,"乐观锁失败", DATA_ACCESS),


    TRANSACTIONAl(2100,"事务出错", DATA_ACCESS),
    ISOLATION(2101,"事务隔离级别错误", TRANSACTIONAl),
    NEVER_TRANSACTIONAL(2102,"方法不支持任何事务", TRANSACTIONAl),

    /**
     * 如: 给一个 id 却没有查到任何数据
     */
    PARAM_ERROR(2003,"参数错误", DATA_ACCESS),


    TEMPLATE(3000,"模板错误",null),
    HTML_TEMPLATE(3001,"HTML模板出错",TEMPLATE),


    DOCUMENT(3200,"文档错误",null),
    DOCUMENT_CONVERT(3201,"文档转换出错",DOCUMENT),
    NO_FONT(3202,"没有找到字体",DOCUMENT),

    EMAIL(3500,"邮件发送出错",null),

    DISTRIBUTE(4000,"分布式问题",null),
    DISTRIBUTE_LOCK(4001,"分布式锁失败",DISTRIBUTE)
    ;

    private final int code;

    private final String display;

    private final ResultCode family;


    private static final Map<Integer, ResultCode> CODE_MAP = CodeEnum.getCodeMap(ResultCode.class);


    @Nullable
    public static ResultCode resolve(int code) {
        return CODE_MAP.get(code);
    }

    ResultCode(int code, @NonNull String display, @Nullable ResultCode family) {
        this.code = code;
        this.display = display;
        this.family = family == null ? this : family;
    }

    @Override
    public int code() {
        return code;
    }

    @NonNull
    @Override
    public String display() {
        return display;
    }

    @NonNull
    @Override
    public ResultCode family() {
        return family;
    }

    public ResultCode rootFamily(){
        ResultCode family = this.family;
        for(;family != family.family;){
            family = family.family;
        }
        return family;
    }



}
