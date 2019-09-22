package org.qinarmy.foundation.core;

import org.springframework.lang.NonNull;

import java.io.PrintWriter;

/**
 *
 * created  on 2019-03-13.
 */
public interface IBusinessException {


    @NonNull
    ResultCode getResultCode();

    /**
     * @see Throwable#getMessage()
     */
    String getMessage();

    /**
     * @see Throwable#getLocalizedMessage()
     */
    String getLocalizedMessage();

    /**
     * @see Throwable#getCause() ()
     */
    Throwable getCause();

    /**
     * @see Throwable#printStackTrace(PrintWriter)
     */
    void printStackTrace(PrintWriter s);

    /**
     * @see Throwable#printStackTrace()
     */
    void printStackTrace();


    static String createMessage(@NonNull String format, Object... args) {
        String msg;
        if (format != null && args != null && args.length > 0) {
            msg = String.format( format, args );
        } else {
            msg = format;
        }
        return msg;
    }

}
