package org.qinarmy.foundation.security;

import org.qinarmy.foundation.core.IBusinessException;

/**
 * created  on 2019-03-13.
 */
public class SecurityKeyException extends Exception {

    public SecurityKeyException(String format, Object... args) {
        super(IBusinessException.createMessage(format, args));
    }

    public SecurityKeyException(Throwable cause, String format, Object... args) {
        super(IBusinessException.createMessage(format, args), cause);
    }
}
