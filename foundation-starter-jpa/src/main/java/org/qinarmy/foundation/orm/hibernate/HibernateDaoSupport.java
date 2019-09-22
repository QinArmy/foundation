package org.qinarmy.foundation.orm.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.query.Query;
import org.qinarmy.foundation.criteria.BaseCriteria;
import org.qinarmy.foundation.orm.BaseDao;
import org.qinarmy.foundation.orm.IDomain;
import org.qinarmy.foundation.orm.NotUniqueException;
import org.qinarmy.foundation.util.Assert;
import org.qinarmy.foundation.util.Pair;
import org.qinarmy.foundation.util.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.util.CollectionUtils;

import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * created  on 10/05/2017.
 */
@SuppressWarnings("all")
public abstract class HibernateDaoSupport implements BaseDao, InitializingBean {


    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public static final Predicate[] EMPTY_PREDICATE = new Predicate[0];


    private HibernateTemplate template;

    @Override
    public final void afterPropertiesSet() throws Exception {
        //notNull( dslContext, "dslContext required" );
        Assert.notNull(template, "template required");
        initBean();
    }

    protected void initBean() {

    }

    public void setTemplate(HibernateTemplate template) {
        this.template = template;
    }

    public final HibernateTemplate getTemplate() {
        return template;
    }


    @Override
    public <D extends IDomain> List<D> find(final Class<D> clazz, final BaseCriteria criteria) {
        return getTemplate().executeWithNativeSession(session -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<D> criteriaQuery = builder.createQuery(clazz);
            Root<D> root = criteriaQuery.from(clazz);

            criteriaQuery.select(root);

            criteriaQuery.where(findPredicate(root, builder, criteria).toArray(EMPTY_PREDICATE));
            criteriaQuery.orderBy(lastIdOrder(root, builder, criteria));

            Query<D> query = session.createQuery(criteriaQuery);

            limit(query, criteria);

            return query.getResultList();
        });
    }


    @Override
    public <D extends IDomain> long getCount(Class<D> clazz, final BaseCriteria criteria) {
        return getTemplate().executeWithNativeSession(session -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<D> root = criteriaQuery.from(clazz);

            Class<?> idClass = root.getModel().getIdType().getJavaType();

            SingularAttribute<? super D, ?> idAttr = root.getModel().getId(idClass);

            criteriaQuery.select(builder.count(root.get(idAttr)));

            criteriaQuery.where(findPredicate(root, builder, criteria).toArray(EMPTY_PREDICATE));

            Query<Long> query = session.createQuery(criteriaQuery);

            Long count;
            return count = query.getSingleResult();

        });
    }


    @Override
    public <D extends IDomain> boolean isExists(Class<D> entityClass, Serializable id) {
        Assert.notNull(entityClass, "entityClass is required.");
        return getTemplate().executeWithNativeSession(session -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQuery = builder.createQuery();
            Root<D> root = criteriaQuery.from(entityClass);

            Class<?> idClass = root.getModel().getIdType().getJavaType();
            SingularAttribute<? super D, ?> idAttr = root.getModel().getId(idClass);

            criteriaQuery.select(root.get(idAttr));

            criteriaQuery.where(
                    builder.equal(root.get(idAttr), id)
            );
            Query<?> query = session.createQuery(criteriaQuery);

            return !CollectionUtils.isEmpty(query.getResultList());
        });
    }

    @Override
    public <D extends IDomain> boolean isExistsByUnique(Class<D> entityClass, String propName,
                                                        Serializable uniqueProp) {
        Assert.notNull(entityClass, "entityClass is required.");
        Assert.notNull(propName, "propName is required.");
        return getTemplate().executeWithNativeSession(session -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQuery = builder.createQuery();
            Root<D> root = criteriaQuery.from(entityClass);

            Class<?> idClass = root.getModel().getIdType().getJavaType();
            SingularAttribute<? super D, ?> idAttr = root.getModel().getId(idClass);

            criteriaQuery.select(root.get(idAttr));

            criteriaQuery.where(
                    builder.equal(root.get(propName), uniqueProp)
            );
            Query<?> query = session.createQuery(criteriaQuery);
            query.setMaxResults(1);

            return !CollectionUtils.isEmpty(query.getResultList());
        });
    }

    @Override
    public <D extends IDomain> boolean isExistsByUnique(Class<D> entityClass, List<String> propNames,
                                                        List<?> uniqueList) {
        Assert.notNull(entityClass, "entityClass is required.");
        Assert.notEmpty(propNames, "propNames not empty");
        Assert.assertNotNull(propNames, "uniqueProp is required.");
        Assert.assertEquals(propNames.size(), uniqueList.size(), "propNames.size not equals uniqueProp.length");
        return getTemplate().executeWithNativeSession(session -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQuery = builder.createQuery();
            Root<D> root = criteriaQuery.from(entityClass);

            Class<?> idClass = root.getModel().getIdType().getJavaType();
            SingularAttribute<? super D, ?> idAttr = root.getModel().getId(idClass);

            criteriaQuery.select(root.get(idAttr));

            List<Predicate> list = new ArrayList<>(propNames.size());

            for (int i = 0; i < uniqueList.size(); i++) {
                list.add(builder.equal(root.get(propNames.get(i)), uniqueList.get(i)));
            }

            criteriaQuery.where(list.toArray(EMPTY_PREDICATE));

            Query<?> query = session.createQuery(criteriaQuery);
            query.setMaxResults(1);
            return !CollectionUtils.isEmpty(query.getResultList());
        });
    }


    @Override
    public void clear() {
        getTemplate().clear();
    }

    @Override
    public Long save(IDomain domain) {
        return (Long) getTemplate().save(domain);
    }


    @Override
    public void update(IDomain domain) {
        getTemplate().update(domain);
    }


    @Override
    public <D extends IDomain> D get(Class<D> entityClass, Serializable id) {
        return getTemplate().get(entityClass, id);
    }

    @Override
    public <D extends IDomain> D getByUnique(Class<D> entityClass, String uniquePropName, Serializable uniqueProp)
            throws NotUniqueException {
        Assert.notNull(entityClass, "entityClass required");
        Assert.notNull(uniquePropName, "uniqueProp required");
        return getTemplate().executeWithNativeSession(session -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<D> criteriaQuery = builder.createQuery(entityClass);
            Root<D> root = criteriaQuery.from(entityClass);

            criteriaQuery.select(root);

            criteriaQuery.where(
                    builder.equal(root.get(uniquePropName), uniqueProp)
            );

            Query<D> query = session.createQuery(criteriaQuery);
            // 最大为2
            query.setMaxResults(2);

            List<D> list;
            list = query.getResultList();

            D d;

            if (list.isEmpty()) {
                d = null;
            } else if (list.size() > 1) {
                throw new NotUniqueException("uniquePropName[%s] and uniqueProp[%s] query result size gt 1"
                        , uniquePropName, uniqueProp);
            } else {
                d = list.get(0);
            }
            return d;
        });
    }

    @Override
    public <D extends IDomain> D getByUnique(Class<D> entityClass, List<String> propNames, List<?> uniqueList)
            throws NotUniqueException {
        Assert.notNull(entityClass, "entityClass is required.");
        Assert.notEmpty(propNames, "propNames not empty");
        Assert.assertNotNull(uniqueList, "uniqueProp is required.");
        Assert.assertEquals(propNames.size(), uniqueList.size(), "propNames.size not equals uniqueProp.length");

        return getTemplate().executeWithNativeSession(session -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<D> criteriaQuery = builder.createQuery(entityClass);
            Root<D> root = criteriaQuery.from(entityClass);

            criteriaQuery.select(root);

            final int size = propNames.size();
            List<Predicate> list = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                list.add(builder.equal(root.get(propNames.get(i)), uniqueList.get(i)));
            }
            criteriaQuery.where(list.toArray(EMPTY_PREDICATE));

            TypedQuery<D> query = session.createQuery(criteriaQuery);
            // 最大为2
            query.setMaxResults(2);

            List<D> resultList;
            resultList = query.getResultList();
            if (list.size() > 1) {
                throw new NotUniqueException("proNames[%s],values[%s] no uqinue", propNames, uniqueList);
            }
            return resultList.isEmpty() ? null : resultList.get(0);
        });
    }


    @Override
    public void flush() {
        getTemplate().flush();
    }

    @Override
    public void evict(IDomain domain) {
        getTemplate().evict(domain);
    }

    @Override
    public void refresh(IDomain domain) {
        getTemplate().refresh(domain);
    }




    protected final void applyNamedParameterToQuery(Query<?> queryObject, String paramName, Object value)
            throws HibernateException {

        if (value instanceof Collection) {
            queryObject.setParameterList(paramName, (Collection<?>) value);
        } else if (value instanceof Object[]) {
            queryObject.setParameterList(paramName, (Object[]) value);
        } else {
            queryObject.setParameter(paramName, value);
        }
    }


    /**
     * 仅支持基本类型
     */
    protected final <T> List<T> wrapperBasic(List<Tuple> tupleList, Class<T> clazz) {
        List<T> list = new ArrayList<>(tupleList.size());
        for (Tuple tuple : tupleList) {
            for (TupleElement<?> tupleElement : tuple.getElements()) {
                list.add(tuple.get(tupleElement.getAlias(), clazz));
            }
        }
        return list;
    }

    protected final <F, S> List<Pair<F, S>> wrapperPairList(List<Tuple> tupleList, Class<F> firstClass
            , Class<S> secondClass) {
        List<Pair<F, S>> list = new ArrayList<>(tupleList.size());
        Pair<F, S> pair;
        for (Tuple tuple : tupleList) {
            list.add(wrapperPair(tuple, firstClass, secondClass));
        }
        return list;
    }

    protected final <F, S> Pair<F, S> wrapperPair(Tuple tuple, Class<F> firstClass
            , Class<S> secondClass) {
        return new Pair<F, S>().setFirst(tuple.get("first", firstClass))
                .setSecond(tuple.get("second", secondClass));
    }


    protected final <T> T beanWrapper(TypedQuery<Tuple> query, Class<T> wrapperClass) {
        T t;
        try {
            Tuple tuple = query.getSingleResult();
            t = BeanUtils.instantiateClass(wrapperClass);

            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(t);
            for (TupleElement<?> e : tuple.getElements()) {
                wrapper.setPropertyValue(e.getAlias(), tuple.get(e.getAlias()));
            }
        } catch (NoResultException e) {
            t = null;
        }
        return t;
    }

    protected final <T> List<T> beanWrapperList(TypedQuery<Tuple> query, Class<T> wrapperClass) {
        return beanWrapperFromTuple(query.getResultList(), wrapperClass);
    }


    protected final <F, S> Pair<F, S> pairWrapper(TypedQuery<Tuple> query) {
        Pair<F, S> pair;
        try {
            pair = pairWrapper(query.getSingleResult());
        } catch (NoResultException e) {
            pair = null;
        }
        return pair;
    }


    private final <F, S> Pair<F, S> pairWrapper(Tuple tuple) {
        Pair<F, S> pair;
        pair = BeanUtils.instantiateClass(Pair.class);

        BeanWrapper wrapper = new BeanWrapperImpl(pair);
        for (TupleElement<?> e : tuple.getElements()) {
            wrapper.setPropertyValue(e.getAlias(), tuple.get(e.getAlias()));
        }
        return pair;
    }

    protected final <F, S> List<Pair<F, S>> pairWrapperList(TypedQuery<Tuple> query) {
        List<Tuple> tupleList = query.getResultList();

        List<Pair<F, S>> pairList = new ArrayList<>(tupleList.size());
        for (Tuple tuple : tupleList) {
            pairList.add(pairWrapper(tuple));
        }
        return pairList;
    }


    protected final <F, S, T> Triple<F, S, T> tripleWrapper(TypedQuery<Tuple> query) {
        Triple<F, S, T> triple;
        try {
            triple = tripleWrapper(query.getSingleResult());
        } catch (NoResultException e) {
            triple = null;
        }
        return triple;
    }


    private final <F, S, T> Triple<F, S, T> tripleWrapper(Tuple tuple) {
        Triple<F, S, T> triple;
        triple = BeanUtils.instantiateClass(Triple.class);

        BeanWrapper wrapper = new BeanWrapperImpl(triple);
        for (TupleElement<?> e : tuple.getElements()) {
            wrapper.setPropertyValue(e.getAlias(), tuple.get(e.getAlias()));
        }
        return triple;
    }

    protected final <F, S, T> List<Triple<F, S, T>> tripleWrapperList(TypedQuery<Tuple> query) {
        List<Tuple> tupleList = query.getResultList();

        List<Triple<F, S, T>> tripleList = new ArrayList<>(tupleList.size());
        for (Tuple tuple : tupleList) {
            tripleList.add(tripleWrapper(tuple));
        }
        return tripleList;
    }


    /**
     * 将 tuple 表示的一行结果包装到 wrapperClass 表示的JavaBean的实例中,并返回实例的列表
     *
     * @param tupleList    （not null）
     * @param wrapperClass (not null), JavaBean 的 class
     * @param <T>          <T> 包装类的类型
     * @return 包装类实例 列表(not null)
     */
    protected final <T> List<T> beanWrapperFromTuple(List<Tuple> tupleList, Class<T> wrapperClass) {

        List<T> list = new ArrayList<>(tupleList.size());
        T t;
        BeanWrapper wrapper;
        int rowNum = 0;
        for (Tuple tuple : tupleList) {

            t = BeanUtils.instantiateClass(wrapperClass);
            wrapper = new BeanWrapperImpl(t);

            for (TupleElement<?> e : tuple.getElements()) {
                wrapper.setPropertyValue(e.getAlias(), tuple.get(e.getAlias()));
            }
            if (wrapper.isWritableProperty("_rowNum_")) {
                wrapper.setPropertyValue("_rowNum_", rowNum);
            }
            list.add(t);
            rowNum++;
        }

        return list;
    }

    protected final void setLastId(BaseCriteria criteria, CriteriaBuilder builder, final Root<?> root) {
        Class<?> idClass = root.getModel().getIdType().getJavaType();
        SingularAttribute<?, ?> idAttr = root.getModel().getId(idClass);

        if (Boolean.TRUE.equals(criteria.getAscOrder())) {
            builder.gt(root.get(idAttr.getName()), criteria.getLastId());
        } else {
            builder.lt(root.get(idAttr.getName()), criteria.getLastId());
        }
    }

    protected final boolean isCreateTime(Field field) {
        return AnnotationUtils.findAnnotation(field, CreationTimestamp.class) != null;
    }


    private <D extends IDomain> List<Predicate> findPredicate(Root<D> root, CriteriaBuilder builder,
                                                              BaseCriteria criteria) {
        List<Predicate> list = new ArrayList<>(3);
        if (hasUeLastId(criteria)) {
            list.add(lastIdPredicate(root, builder, criteria));
        }
        if (criteria.getStartCreateTime() != null) {
            list.add(builder.greaterThanOrEqualTo(root.get("createTime"), criteria.getStartCreateTime()));
        }
        if (criteria.getEndCreateTime() != null) {
            list.add(builder.lessThanOrEqualTo(root.get("createTime"), criteria.getEndCreateTime()));
        }
        return list;
    }

    protected final boolean hasUeLastId(BaseCriteria criteria) {
        return criteria.getLastId() != null && criteria.getLastId() > -1L;
    }

    protected final <D extends IDomain> Predicate lastIdPredicate(Root<D> root, CriteriaBuilder builder,
                                                                  BaseCriteria criteria) {
        return Boolean.TRUE.equals(criteria.getAscOrder())
                ? builder.greaterThan(root.get("id"), criteria.getLastId())
                : builder.lessThan(root.get("id"), criteria.getLastId());
    }

    protected final <D extends IDomain> Order lastIdOrder(Root<D> root, CriteriaBuilder builder,
                                                          BaseCriteria criteria) {
        return Boolean.TRUE.equals(criteria.getAscOrder())
                ? builder.asc(root.get("id"))
                : builder.desc(root.get("id"));

    }

    protected final <D extends IDomain> void limit(javax.persistence.Query query, BaseCriteria criteria) {
        if (!hasUeLastId(criteria)) {
            query.setFirstResult(criteria.getOffset());
        }
        query.setMaxResults(criteria.getRowCount());
    }


}
