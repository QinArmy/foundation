package org.qinarmy.foundation.orm;

import org.qinarmy.foundation.sequence.Snowflake;

import java.lang.annotation.*;

/**
 * 用于在实体上定义 id 乱雪花算法的起始时间
 * created  on 2019-03-16.
 * @see SnowflakeIdentifierGenerator
 * @see Snowflake
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SnowflakeTime {

    /**
     *
     * @see Snowflake#getStartTime()
     */
    long value();
}
