package org.qinarmy.foundation.datasource;

import org.qinarmy.foundation.tx.TransactionDefinitionHolder;
import org.qinarmy.foundation.tx.TransactionDefinitionInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 路由数据源,用于实现读写分离
 * created  on 2019-03-17.
 *
 * @see TransactionDefinitionHolder
 * @see org.springframework.transaction.annotation.Transactional
 * @see TransactionDefinitionInterceptor
 */
public class PrimarySecondaryRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger LOG = LoggerFactory.getLogger(PrimarySecondaryRoutingDataSource.class);

    public static final String PRIMARY = DataSourceRole.PRIMARY.toString();

    public static final String SECONDARY = DataSourceRole.SECONDARY.toString();

    public static final String TIMEOUT_SECONDARY = DataSourceRole.TIMEOUT.toString();

    /**
     * 当事务超时时间大于等于此值时使用超时从库
     */
    private int timeoutBoundary = 10;


    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String lookupKey;
        if (TransactionDefinitionHolder.isReadOnly()) {
            if (TransactionDefinitionHolder.getTimeout() >= getTimeoutBoundary()) {
                lookupKey = TIMEOUT_SECONDARY;
            } else {
                lookupKey = SECONDARY;
            }
        } else {
            lookupKey = PRIMARY;
        }
        LOG.debug("datasource : {},thread :{}", lookupKey, TransactionDefinitionHolder.getName());
        return lookupKey;
    }

    public int getTimeoutBoundary() {
        return timeoutBoundary;
    }

    public void setTimeoutBoundary(int timeoutBoundary) {
        this.timeoutBoundary = timeoutBoundary;
    }
}
