package org.qinarmy.foundation.orm;

import org.hibernate.HibernateException;
import org.hibernate.annotations.Type;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.LoggableUserType;
import org.qinarmy.foundation.struct.CodeEnum;
import org.qinarmy.foundation.struct.CodeEnumHelper;
import org.qinarmy.foundation.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * created  on 2018/6/2.
 */
public class CodeEnumUserType<T extends Enum<T> & CodeEnum> implements EnhancedUserType,
        DynamicParameterizedType, LoggableUserType, Serializable {

    private static final long serialVersionUID = 8704615612794325874L;

    private static final Logger LOG = LoggerFactory.getLogger(CodeEnumUserType.class);


    private static final ConcurrentMap<Class<?>, CodeEnumUserType<?>> INSTANCE_MAP = new ConcurrentHashMap<>();


    private Class<T> codeEnumType;



    private final AtomicBoolean VALIDATE = new AtomicBoolean(false);


    private final int[] SQL_TYPE = new int[]{Types.INTEGER};

    // hibernate invoke
    public CodeEnumUserType() {

    }

    public CodeEnumUserType(Class<T> codeEnumType) {
        this.codeEnumType = codeEnumType;
        //validate CodeEnum
        if(VALIDATE.compareAndSet(false,true)){
            CodeEnum.getCodeMap(codeEnumType);
        }

    }


    @SuppressWarnings("all")
    public <T extends Enum<T> & CodeEnum> CodeEnumUserType<T> getInstance(Class<T> codeEnumType) {

        CodeEnumUserType<T> enumType = (CodeEnumUserType<T>) INSTANCE_MAP.get(codeEnumType);
        if (enumType == null) {
            enumType = new CodeEnumUserType<>(codeEnumType);
            INSTANCE_MAP.putIfAbsent(codeEnumType, enumType);
        }
        return enumType;
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) {
        if (value == null) {
            return null;
        }
        return value.getClass().getName() + "." + ((CodeEnum) value).name();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setParameterValues(Properties parameters) {
        //仅支持 注解配置
        final ParameterType reader = (ParameterType) parameters.get(DynamicParameterizedType.PARAMETER_TYPE);
        assertParameterType(reader);

        codeEnumType = reader.getReturnedClass().asSubclass(CodeEnum.class);
        if (!codeEnumType.isEnum()) {
            throw new IllegalStateException(String.format("class[%s] isn't Enum", codeEnumType));
        }
        //validate CodeEnum
        if(VALIDATE.compareAndSet(false,true)){
            CodeEnum.getCodeMap(codeEnumType);
        }

    }

    private void assertParameterType(ParameterType reader) throws IllegalStateException, IllegalArgumentException {
        if (reader == null) {
            throw new IllegalStateException(String.format("%s 只支持注解配置", getClass().getName()));
        }
        if (!CodeEnum.class.isAssignableFrom(reader.getReturnedClass())) {
            throw new IllegalArgumentException(String.format("被 %s 标注的 %s 不是 %s 的子类"
                    , Type.class.getName()
                    , reader.getReturnedClass().getName()
                    , CodeEnum.class.getName()));
        }
    }


    @Override
    public int[] sqlTypes() {
        return SQL_TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> returnedClass() {
        return codeEnumType;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        Assert.notNull(codeEnumType, "type not config");
        Map<Integer, T> codeMap = CodeEnumHelper.getMap(codeEnumType);
        if (codeMap == null) {
            throw new HibernateException(String.format("CodeEnum[%s] error", codeEnumType.getName()));
        }
        return codeMap.get(rs.getInt(names[0]));
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        Assert.notNull(codeEnumType, "type not config");
        if (value == null) {
            st.setNull(index, Types.INTEGER);
        } else {
            st.setInt(index, ((CodeEnum) value).code());
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public String objectToSQLString(Object value) {
        Assert.notNull(codeEnumType, "type not config");
        return String.valueOf(((CodeEnum) value).code());
    }

    @Override
    public String toXMLString(Object value) {
        return ((CodeEnum) value).name();
    }

    @Override
    public Object fromXMLString(String xmlValue) {
        Assert.notNull(codeEnumType, "type not config");
        return Enum.valueOf(codeEnumType,xmlValue);
    }


    /*################# private ##########################*/


}
