package org.qinarmy.foundation.data;

import org.qinarmy.foundation.tx.TransactionDefinitionHolder;
import org.qinarmy.foundation.tx.TransactionDefinitionInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 路由数据源,用于实现读写分离
 * created  on 2019-03-17.
 * @see TransactionDefinitionHolder
 * @see org.springframework.transaction.annotation.Transactional
 * @see TransactionDefinitionInterceptor
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger LOG = LoggerFactory.getLogger( RoutingDataSource.class );

    private static final String MASTER_PATTER = "m%s";

    private static final String SLAVER_PATTER = "s%s";


    @Override
    protected Object determineCurrentLookupKey() {
        String pattern;
        if (TransactionDefinitionHolder.isReadOnly()) {
            pattern = SLAVER_PATTER;
        } else {
            pattern = MASTER_PATTER;
        }
        String lookupKey = String.format( pattern, "0" );
        LOG.debug( "datasource : {},thread :{}", lookupKey, Thread.currentThread().getName() );
        return lookupKey;
    }
}
