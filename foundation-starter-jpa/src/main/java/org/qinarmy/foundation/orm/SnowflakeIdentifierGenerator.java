package org.qinarmy.foundation.orm;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.qinarmy.foundation.sequence.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 实体类可以通过 {@link SnowflakeTime} 指定雪花算法的开始时间
 * </p>
 * created  on 2019-03-15.
 *
 * @see Snowflake
 * @see SnowflakeTime
 */
public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(SnowflakeIdentifierGenerator.class);

    private static final String CONFIG_LOCATION = "classpath*:identifier/snowflake/snowflakeTime.properties";

    private static final String DEFAULT_START_TIME_KEY = "org.qinarmy.foundation.snowflake.defaultStartTime";

    //默认开始时间
    private static final long DEFAULT_START_TIME = initDefaultStartTime();


    private static final AtomicLong WORK_ID = new AtomicLong(0L);

    private static final AtomicLong DATA_CENTER_ID = new AtomicLong(0L);

    private static final AtomicReference<Snowflake> DEFAULT_SNOWFLAKE_INIT = new AtomicReference<>(null);

    private static Snowflake SNOWFLAKE;

    private static final AtomicReference<Snowflake> SNOWFLAKE_INIT = new AtomicReference<>(null);

    private Snowflake snowflake;


    private static long initDefaultStartTime() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                SnowflakeIdentifierGenerator.class.getClassLoader());
        try {
            Resource[] resources = resolver.getResources(CONFIG_LOCATION);

            Properties properties = new Properties();
            properties.load(resources[0].getInputStream());

            String startTimeValue = properties.getProperty(DEFAULT_START_TIME_KEY);
            return Long.parseLong(startTimeValue);
        } catch (Exception e) {
            throw new RuntimeException(String.format("not found %s", CONFIG_LOCATION), e);
        }
    }


    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object)
            throws HibernateException {
        if (snowflake == null) {
            initSnowflake(object.getClass());
        }
        return snowflake.next();
    }


    public static long next() {
        if (SNOWFLAKE == null) {
            initDefaultSnowflake();
        }
        return SNOWFLAKE.next();
    }

    public static String nextAsString() {
        if (SNOWFLAKE == null) {
            initDefaultSnowflake();
        }
        return String.valueOf(SNOWFLAKE.next());
    }


    public static void setDataCenterIdAndWorkId(long dataCenterId, long workId) {

        final boolean dataCenterSuccess = DATA_CENTER_ID.compareAndSet(0L, dataCenterId);
        final boolean workIdSuccess = WORK_ID.compareAndSet(0L, workId);

        LOG.info("snowflake update data center id success:{},current value:{}",
                dataCenterSuccess,
                DATA_CENTER_ID.get()
        );

        LOG.info("snowflake update work id success:{},current value:{}",
                workIdSuccess,
                WORK_ID.get()
        );

        initDefaultSnowflake();
    }

    private void initSnowflake(Class<?> domainClass) {

        SnowflakeTime snowflakeTime = AnnotationUtils.getAnnotation(domainClass, SnowflakeTime.class);

        final long startTime = snowflakeTime == null ? DEFAULT_START_TIME : snowflakeTime.value();

        this.snowflake = init(SNOWFLAKE_INIT, startTime);

        LOG.info("create Snowflake, startTime[{}],dataCenter[{}],workId[{}]",
                startTime, this.snowflake.getDataCenterId(), this.snowflake.getWorkerId());
    }

    private static void initDefaultSnowflake() {
        SNOWFLAKE = init(DEFAULT_SNOWFLAKE_INIT, DEFAULT_START_TIME);
    }

    private static Snowflake init(AtomicReference<Snowflake> initor, final long startTime) {
        return initor.updateAndGet(t -> {
            Snowflake s = t;
            if (s == null) {
                s = Snowflake.createInstance(startTime, WORK_ID.get(), DATA_CENTER_ID.get());
            }
            return s;
        });
    }


}
