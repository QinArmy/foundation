package org.qinarmy.foundation.util;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.tx.ArmyDataAccessException;
import org.qinarmy.foundation.tx.OptimisticLockingException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * created  on 08/04/2018.
 */
public abstract class ExceptionUtils {


    public static Throwable getRootException(Throwable e) {
        Throwable t, t2 = e;
        t = t2;
        while (t2 != null) {
            t = t2;
            t2 = t2.getCause();
        }
        return t;
    }

    /**
     * 将异常栈信息打入字符串
     */
    public static String getExceptionStack(Throwable throwable) {
        try (StringWriter stringWriter = new StringWriter(); PrintWriter writer = new PrintWriter(stringWriter)) {

            throwable.printStackTrace(writer);

            return stringWriter.toString();

        } catch (Exception e) {
            return String.format("异常栈打印失败! %s", throwable.getMessage());
        }

    }

    public static String formatMsg(UnrecognizedPropertyException e) {
        StringBuilder builder = new StringBuilder();
        builder.append("property '")
                .append(e.getPropertyName())
                .append("' is unrecognized field,known properties are [")
        ;
        for (Object o : e.getKnownPropertyIds()) {
            builder.append(o)
                    .append(",");
        }
        builder.deleteCharAt(builder.length() - 1)
                .append("]");
        return builder.toString();
    }

    public static String getSimpleMessage(Throwable e) {
        Throwable root = ExceptionUtils.getRootException(e);
        String msg;
        if (root instanceof UnrecognizedPropertyException) {
            msg = formatMsg((UnrecognizedPropertyException) root);
        } else {
            msg = root.getMessage();
        }
        //InvalidFormatException
        return msg;
    }

    public static ArmyDataAccessException convert(@NonNull DataAccessException de, @Nullable String message) {
        ArmyDataAccessException e;
        String msg = message == null ? de.getMessage() : message;
        if (de instanceof OptimisticLockingFailureException) {
            e = new OptimisticLockingException(msg , de);
        } else {
            e = new ArmyDataAccessException(ResultCode.DATA_ACCESS,msg, de);
        }
        return e;
    }

}
