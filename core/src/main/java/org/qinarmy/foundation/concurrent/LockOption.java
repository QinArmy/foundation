package org.qinarmy.foundation.concurrent;


import org.qinarmy.foundation.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.qinarmy.foundation.util.Assert.assertHasText;


/**
 * created  on 2018/9/8.
 */
public class LockOption {

    private static final long DEFAULT_SECOND = 30L;

    /**
     * 用于唯一标的分布式锁
     */
    public final String key;

    public final List<String> keyList;

    /**
     * 尝试获取锁的持有者唯一标识
     */
    public final String holder;

    /**
     * 表示 在 second 后 锁自动释放
     */
    private long second = DEFAULT_SECOND;


    public LockOption(String key, String holder) {
        assertHasText( key, "key required" );
        assertHasText( holder, "holder required" );
        this.key = key;
        this.holder = holder;
        keyList = Collections.singletonList( this.key );
    }

    public LockOption(String key) {
        this( key, UUID.randomUUID().toString() );
    }

    public String getKey() {
        return key;
    }

    public String getHolder() {
        return holder;
    }

    public long getSecond() {
        if (second < 1L) {
            second = DEFAULT_SECOND;
        }
        return second;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return JsonUtils.writeToJson(this);

    }
}
