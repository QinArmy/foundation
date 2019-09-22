package org.qinarmy.foundation.orm;


import org.qinarmy.foundation.criteria.BaseCriteria;

import java.io.Serializable;
import java.util.List;

/**
 * created  on 2019-03-17.
 */
public interface BaseDao {


    <D extends IDomain> D get(Class<D> entityType, Serializable id);

    <D extends IDomain> boolean isExists(Class<D> entityType, Serializable id);

    <D extends IDomain> boolean isExistsByUnique(Class<D> entityClass, String propName, Serializable uniqueProp);

    <D extends IDomain> boolean isExistsByUnique(Class<D> entityType, List<String> nameList, List<?> uniqueList);

    Long save(IDomain domain);

    <D extends IDomain> D getByUnique(Class<D> entityClass, String uniquePropName, Serializable uniqueProp)
            throws NotUniqueException;

    <D extends IDomain> D getByUnique(Class<D> entityClass, List<String> nameList, List<?> uniqueList)
            throws NotUniqueException;

    <D extends IDomain> List<D> find(Class<D> entityClass, final BaseCriteria criteria);

    <D extends IDomain> long getCount(Class<D> entityClass, final BaseCriteria criteria);


    void update(IDomain domain);

    void clear();

    void flush();

    void evict(IDomain domain);

    void refresh(IDomain domain);

    default Class<?> getEntityType() {
        return IDomain.class;
    }
}
