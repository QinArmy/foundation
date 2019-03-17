package org.qinarmy.foundation.util;

import org.springframework.core.env.Environment;

import java.time.LocalDateTime;

/**
 * created  on 2019-03-17.
 */
public abstract class EnvironmentUtils {

    private static Environment env;

    public static boolean isOffDuration(String durationKey) {
        if (!StringUtils.hasText(durationKey)) {
            return false;
        }
        LocalDateTime[] duration = env.getProperty(durationKey, LocalDateTime[].class, ArrayUtils.EMPTY_DATE_TIME);
        boolean off = false;
        if (duration.length == 2) {
            LocalDateTime now = LocalDateTime.now();
            // 数组元素不会为 null ,因为自定义的转换器不允许数组元素为 null
            off = !now.isBefore(duration[0]) && now.isBefore(duration[1]);
        }
        return off;
    }


    /**
     * 系统启动时调用 .
     */
    public static void setEnvironment(Environment environment) {
        EnvironmentUtils.env = environment;
    }

}
