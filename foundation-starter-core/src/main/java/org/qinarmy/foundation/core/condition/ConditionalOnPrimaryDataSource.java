package org.qinarmy.foundation.core.condition;


import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.lang.annotation.*;


/**
 * 当 上下文中存在 {@link AbstractRoutingDataSource} bean 且有 {@code hasPrimary} 可读属性
 * 且返回 {@code true} 表示有主库的存在,则条件匹配
 * <p>
 * 因为是查看 bean 是否存在 ,所以通常与 {@link AutoConfigureBefore} 或 {@link AutoConfigureAfter} 联用.
 * </p>
 * <p>
 * 为保证此注解能正常工作,相应的路由数据源的 Bean 定义方法的返回值必须是 {@link AbstractRoutingDataSource} 子类.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnPrimaryDataSourceCondition.class)
public @interface ConditionalOnPrimaryDataSource {

}
