package themeleaf;

import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.core.RuntimeBusinessException;

/**
 * 当 {@link ThymeleafTemplateEngine} 出错时抛出.
 * created  on 2018-12-19.
 */
public class TemplateEngineException extends RuntimeBusinessException {

    private static final long serialVersionUID = 4120948366376746544L;

    public TemplateEngineException(ResultCode resultCode) {
        super(resultCode);
    }

    public TemplateEngineException(ResultCode resultCode, String format, Object... args) {
        super(resultCode, format, args);
    }

    public TemplateEngineException(ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(resultCode, cause, format, args);
    }
}
