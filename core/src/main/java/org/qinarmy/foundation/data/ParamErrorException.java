package org.qinarmy.foundation.data;


import org.qinarmy.foundation.core.ResultCode;

/**
 * 如:当给的参数不能查出任何数据时抛出》
 * created  on 2019-03-17.
 */
public class ParamErrorException extends ParamException {


    private static final long serialVersionUID = -3258233409107665672L;

    public ParamErrorException(String format, Object... args) {
        super(ResultCode.PARAM_ERROR, format, args);
    }

    public ParamErrorException( Throwable cause, String format, Object... args) {
        super(ResultCode.PARAM_ERROR, cause, format, args);
    }
}
