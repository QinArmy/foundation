package org.qinarmy.foundation.env;

import org.qinarmy.foundation.util.Assert;
import org.qinarmy.foundation.util.CollectionUtils;
import org.qinarmy.foundation.util.StringUtils;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.qinarmy.foundation.util.ArrayUtils.EMPTY_DATE_TIME;

/**
 * @see CoreEnvironmentPostProcessor
 * @see EnvironmentPostProcessor
 * created  on 2019-03-17.
 */
public abstract class EnvironmentUtils {

    private static Environment env;

    /**
     * 用于开关多数时间是 打开 的情况
     */
    public static boolean isOffDuration(String durationKey) {
        if (!StringUtils.hasText(durationKey)) {
            return false;
        }
        Assert.state(durationKey.endsWith(".off.duration"), "durationKey 没有以 .off.duration 为后缀");
        return isMatchDuration(durationKey);
    }

    /**
     * 用于开关多数时间是 关闭 的情况
     *
     * @param durationKey 必须以 {@code on.duration} 为后缀
     */
    public static boolean isOnDuration(String durationKey) {
        if (!StringUtils.hasText(durationKey)) {
            return false;
        }
        Assert.state(durationKey.endsWith(".on.duration"), "durationKey 没有以 .on.duration 为后缀");
        return isMatchDuration(durationKey);
    }

    /**
     * 查询 kye 所表示的集合中是否包含 target
     *
     * @return true 包含
     */
    public static boolean contains(String key, String target) {
        String[] array = env.getProperty(key, String[].class);
        if (array == null) {
            return false;
        }
        return doContains(target, array);
    }

    /**
     * 查询 kye 所表示的集合中是否包含 target
     *
     * @return true 包含
     */
    public static boolean contains(String key, Long target) {
        Long[] array = env.getProperty(key, Long[].class);
        if (array == null) {
            return false;
        }
        return doContains(target, array);
    }

    /**
     * 查询 kye 所表示的集合中是否包含 target
     *
     * @return true 包含
     */
    public static boolean contains(String key, Integer target) {
        Integer[] array = env.getProperty(key, Integer[].class);
        if (array == null) {
            return false;
        }
        return doContains(target, array);
    }


    /**
     * 读取配置并转换为相应的 List
     *
     * @param typeClass 数组类型
     * @return 不可变 List,若无则返回 empty list
     */
    public static <T> List<T> getList(String key, Class<T[]> typeClass) {
        T[] array = env.getProperty(key, typeClass);
        if (ObjectUtils.isEmpty(array)) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(array.length);
        Collections.addAll(list, array);
        return Collections.unmodifiableList(list);
    }


    /**
     * 读取配置并转换为相应的 List
     *
     * @param typeClass 数组类型
     * @return 不可变 List,若无或是empty list 则返回 defaultList
     */
    public static <T> List<T> getList(String key, Class<T[]> typeClass, List<T> defaultList) {
        List<T> list = getList(key, typeClass);
        return CollectionUtils.isEmpty(list) ? defaultList : list;
    }

    public static <T> List<T> getRequiredList(String key, Class<T[]> typeClass) {
        List<T> list = getList(key, typeClass);
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalStateException(String.format("Required key '%s' not found", key));
        }
        return list;
    }

    /**
     * 读取配置并转换为相应的 Set
     *
     * @param typeClass 数组类型
     * @return 不可变集合
     */
    public static <T> Set<T> getSet(String key, Class<T[]> typeClass) {
        T[] array = env.getProperty(key, typeClass);
        if (ObjectUtils.isEmpty(array)) {
            return Collections.emptySet();
        }
        Set<T> set = new HashSet<>((int) (array.length / 0.75f));
        Collections.addAll(set, array);
        return Collections.unmodifiableSet(set);
    }

    /**
     * 读取配置并转换为相应的 Set
     *
     * @param typeClass 数组类型
     * @return 不可变集合, , 若无或是empty set 则返回 defaultSet
     */
    public static <T> Set<T> getSet(String key, Class<T[]> typeClass, Set<T> defaultSet) {
        Set<T> set = getSet(key, typeClass);
        return CollectionUtils.isEmpty(set) ? defaultSet : set;
    }

    public static <T> Set<T> getRequiredSet(String key, Class<T[]> typeClass) {
        Set<T> set = getSet(key, typeClass);
        if (CollectionUtils.isEmpty(set)) {
            throw new IllegalStateException(String.format("Required key '%s' not found", key));
        }
        return set;
    }

    /*########################### 以下是 private 方法 ##############################################*/

    private static <T> boolean doContains(T target, T[] array) {
        // 这里不用 set 是因为 array 加入 set 也需要遍历一次
        boolean yes = false;
        for (T e : array) {
            if (ObjectUtils.nullSafeEquals(target, e)) {
                yes = true;
                break;
            }
        }
        return yes;
    }

    private static boolean isMatchDuration(String durationKey) {
        //@see org.qinarmy.foundation.convert.Converters#StringToLocalDatetime
        LocalDateTime[] duration = env.getProperty(durationKey, LocalDateTime[].class, EMPTY_DATE_TIME);
        boolean match = false;
        if (duration.length == 2) {
            LocalDateTime now = LocalDateTime.now();
            // 数组元素不会为 null ,因为自定义的转换器不允许数组元素为 null
            match = !now.isBefore(duration[0]) && now.isBefore(duration[1]);
        }
        return match;
    }

    /**
     * 系统启动时调用 .
     */
    static void setEnvironment(Environment environment) {
        EnvironmentUtils.env = environment;
    }

}
