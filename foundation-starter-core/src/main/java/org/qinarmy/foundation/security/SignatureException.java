package org.qinarmy.foundation.security;

import org.qinarmy.foundation.core.IBusinessException;

public class SignatureException extends Exception {

    public SignatureException(Throwable cause, String format, Object... args) {
        super(IBusinessException.createMessage(format, args), cause);
    }
}
