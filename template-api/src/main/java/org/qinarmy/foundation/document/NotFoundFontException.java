package org.qinarmy.foundation.document;


import org.qinarmy.foundation.core.ResultCode;

/**
 * 当没有找到源文档的字体时抛出.
 * created  on 2018-12-20.
 *
 * @see DocumentConverter
 */
public class NotFoundFontException extends DocumentConvertException {


    private static final long serialVersionUID = 3784775437850451866L;


    public NotFoundFontException(String format, Object... args) {
        super(ResultCode.NO_FONT, format, args);
    }

    public NotFoundFontException( Throwable cause, String format, Object... args) {
        super(ResultCode.NO_FONT, cause, format, args);
    }
}
