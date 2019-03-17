package org.qinarmy.foundation.concurrent;


import org.qinarmy.foundation.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.qinarmy.foundation.util.Assert.assertNotNull;


/**
 * 这个类是 {@link DistributeLock} 的一个使用 redis 实现的分布式锁
 * created  on 2018/9/8.
 */
public class RedisDistributeLock implements DistributeLock, InitializingBean {

    private static final Logger LOG  = LoggerFactory.getLogger( RedisDistributeLock.class );

    private static final String LOCK_SCRIPT_PATTEREN = "classpath*:/script/redis/tasty/lock.lua";

    private static final String RELEASE_LOCK_SCRIPT_PATTERN = "classpath*:/script/redis/tasty/releaseLock.lua";


    private final RedisScript<String> LOCK_SCRIPT;


    private final RedisScript<String> RELEASE_LOCK_SCRIPT;

    private StringRedisTemplate template;

    @Override
    public void afterPropertiesSet() throws Exception {
        assertNotNull( template, "template required" );
    }

    /**
     * @see Class#getClassLoader()
     */
    public RedisDistributeLock() {
        LOCK_SCRIPT = new DefaultRedisScript<>(
                ResourceUtils.extractFileText( LOCK_SCRIPT_PATTEREN, getClass() ),String.class );
        RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>(
                ResourceUtils.extractFileText( RELEASE_LOCK_SCRIPT_PATTERN, getClass() ),String.class  );

    }

    public RedisDistributeLock(ResourcePatternResolver resolver) throws IOException {

        Resource[] resources = resolver.getResources( LOCK_SCRIPT_PATTEREN );

        try (InputStream in = resources[0].getInputStream()) {
            LOCK_SCRIPT = new DefaultRedisScript<>( StreamUtils.copyToString( in, StandardCharsets.UTF_8 )
                    ,String.class
            );
        }

        resources = resolver.getResources( RELEASE_LOCK_SCRIPT_PATTERN );

        try (InputStream in = resources[0].getInputStream()) {
            RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>( StreamUtils.copyToString( in, StandardCharsets.UTF_8 )
                    ,String.class
            );
        }
    }


    @Override
    public boolean tryLock(LockOption option) {
        String actualHolder;

        actualHolder = template.execute( LOCK_SCRIPT, option.keyList
                , option.holder, String.valueOf( option.getSecond() ) );
        boolean success = ObjectUtils.nullSafeEquals( option.holder, actualHolder );

        LOG.info( "success:{}, lock actual holder is {} , {} try lock",success,actualHolder,option.holder );
        return success;
    }

    @Override
    public boolean releaseLock(LockOption option) {
        String actualHolder;
        actualHolder = template.execute( RELEASE_LOCK_SCRIPT, option.keyList, option.holder );
        boolean success = ObjectUtils.nullSafeEquals( option.holder, actualHolder );

        LOG.info( "success:{}, lock actual holder is {} , {} try releaseLock",success,actualHolder,option.holder );
        return success;
    }

    public RedisDistributeLock setTemplate(StringRedisTemplate template) {
        this.template = template;
        return this;
    }

    @Override
    public String toString() {
        return "lock[\n" + LOCK_SCRIPT.getScriptAsString() + "\n]\n\n,release[\n"
                + RELEASE_LOCK_SCRIPT.getScriptAsString() + "\n]";
    }
}
