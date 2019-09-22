package themeleaf;

import org.qinarmy.foundation.util.Pair;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * created  on 2018-12-19.
 *
 * @see ThymeleafTemplateEngine
 */
public class EngineForm {


    /**
     * 模板名,可加以下前缀
     * <ul>
     * <li>{@code classpath:} 即扫本模块的 jar </li>
     * <li>{@code classpath*:} 即扫瞄路径中的所有 jar </li>
     * <li>{@code file:} 文件系统</li>
     * <li>{@code env:} {@link org.springframework.core.env.Environment} 中读取</li>
     * </ul>
     * 前缀,例: {@code classpath*:non-web/template}
     */
    @NonNull
    private String templateName;

    /**
     * 用于替换模板的变量. 与 {@link #variableBean} 不能同时为 null,优先使用 variables
     */
    @Nullable
    private Map<String, Object> variables;

    /**
     * 用于替换模板的变量. 与 {@link #variables} 不能同时为 null,优先使用 {@link #variables}
     */
    @Nullable
    private Object variableBean;

    /**
     * 当生成的是 pdf 时使用,以向 pdf 中加入资源,如:图片.
     */
    //@Nullable
    private List<Pair<String, Resource>> resourceList;


    /**
     * @see #templateName
     */
    @NonNull
    public String getTemplateName() {
        return templateName;
    }

    public EngineForm setTemplateName(@NonNull String templateName) {
        this.templateName = templateName;
        return this;
    }

    @Nullable
    public Map<String, Object> getVariables() {
        return variables;
    }

    public EngineForm setVariables(@Nullable Map<String, Object> variables) {
        this.variables = variables;
        return this;
    }

    @Nullable
    public Object getVariableBean() {
        return variableBean;
    }

    public EngineForm setVariableBean(@Nullable Object variableBean) {
        this.variableBean = variableBean;
        return this;
    }

    @Nullable
    private List<Pair<String, Resource>> getResourceList() {
        return resourceList;
    }

    private EngineForm setResourceList(@Nullable List<Pair<String, Resource>> resourceList) {
        this.resourceList = resourceList;
        return this;
    }


}
