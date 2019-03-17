package org.qinarmy.foundation.document;

import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.core.RuntimeBusinessException;

/**
 * 文档转换出错时抛出.
 * created  on 2018-12-20.
 *
 * @see DocumentConverter
 */
public class DocumentConvertException extends RuntimeBusinessException {

    private static final long serialVersionUID = 2282761372289139748L;

    public DocumentConvertException(ResultCode resultCode) {
        super(resultCode);
    }

    public DocumentConvertException(ResultCode resultCode, String format, Object... args) {
        super(resultCode, format, args);
    }

    public DocumentConvertException(ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(resultCode, cause, format, args);
    }
}
