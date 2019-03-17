package themeleaf;

import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 这个接口是 对 模板 库的抽象,如: Thymeleaf.
 * 将会利用底层模板 和 变量生成相应的 html or pdf
 * created  on 2018-12-19.
 *
 * @see org.thymeleaf.ITemplateEngine
 * @see <a href=https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html">Thymeleaf Documentation: SET</a>
 */
public interface ThymeleafTemplateEngine {


    /**
     * 将指定的 html 模块使用指定的变量替换
     *
     * @throws TemplateEngineException 模板转换出错
     */
    @NonNull
    Resource render(@NonNull EngineForm form) throws TemplateEngineException;


    /**
     * 将指定的 html 模块使用指定的变量替换
     *
     * @throws TemplateEngineException 模板转换出错
     */
    @Nullable
    Resource renderAsPdf(@NonNull EngineForm form) throws TemplateEngineException;


    /**
     * 将指定的 html 模块使用指定的变量替换
     *
     * @throws TemplateEngineException 模板转换出错
     */
    @Nullable
    String renderAsString(@NonNull EngineForm form) throws TemplateEngineException;


}
