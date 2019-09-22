package org.qinarmy.foundation.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * created  on 2019-03-17.
 */
public abstract class ResourceUtils extends org.springframework.util.ResourceUtils {

    public static final String ENV_PREFIX = "env:";
    /**
     * 表示 云存储中获取资源.
     */
    public static final String CLOUD_PREFIX = "cloud:";


    private static ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver(
            ClassUtils.getDefaultClassLoader());


    public static final Set<String> RESOURCE_PREFIX_SET = ArrayUtils.asUnmodifiableSet(
            CLASSPATH_URL_PREFIX,
            FILE_URL_PREFIX,
            JAR_URL_PREFIX,
            ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX,
            ENV_PREFIX,
            CLOUD_PREFIX
    );

    public static boolean containsPrefix(String location) {
        if (!StringUtils.hasText(location)) {
            return false;
        }
        int index = location.indexOf(':');
        boolean contains = false;
        if (index > 0) {
            contains = RESOURCE_PREFIX_SET.contains(location.substring(0, index + 1));
        }
        return contains;
    }

    /**
     * 提取 resourcePattern 指定下的文件的文本内容
     *
     * @param resourcePattern classpath: 为前缀
     * @param contextClass    调用此方法的类(也可以是其它类),用于获取 {@link ClassLoader}
     * @return 文件内容
     */
    public static String extractFileText(String resourcePattern, Class<?> contextClass) throws RuntimeException {

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                    contextClass.getClassLoader());
            Resource[] resources = resolver.getResources(resourcePattern);
            try (InputStream in = resources[0].getInputStream()) {

                return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static Resource[] getResources(String pattern)throws IOException {
       return RESOURCE_PATTERN_RESOLVER.getResources(pattern);
    }

}
