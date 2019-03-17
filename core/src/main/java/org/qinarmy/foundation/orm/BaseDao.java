package org.qinarmy.foundation.orm;

import org.qinarmy.army.domain.IDomain;
import org.qinarmy.foundation.criteria.BaseCriteria;

import java.io.Serializable;
import java.util.List;

/**
 * created  on 2019-03-17.
 */
public interface BaseDao {


    <D extends IDomain<D>> D get(Class<D> entityType, Serializable id);

    <D extends IDomain<D>> boolean isExists(Class<D> entityType, Serializable id);

    <D extends IDomain<D>> boolean isExistsByUnique(Class<D> entityClass, String propName, Serializable uniqueProp);

    <D extends IDomain<D>> boolean isExistsByUnique(Class<D> entityType, List<String> nameList, List<?> uniqueList);

    Serializable save(IDomain<?> domain);

    <D extends IDomain<D>> D getByUnique(Class<D> entityClass, String uniquePropName, Serializable uniqueProp)
            throws NotUniqueException;

    <D extends IDomain<D>> D getByUnique(Class<D> entityClass, List<String> nameList, List<?> uniqueList)
            throws NotUniqueException;

    <D extends IDomain<D>> List<D> find(Class<D> entityClass, final BaseCriteria criteria);

    <D extends IDomain<D>> long getCount(Class<D> entityClass, final BaseCriteria criteria);


    void update(IDomain<?> domain);

    void clear();

    void flush();

    void evict(IDomain<?> domain);

    void refresh(IDomain<?> domain);

    default Class<?> getEntityType() {
        return Object.class;
    }
}
