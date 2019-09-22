package org.qinarmy.foundation.orm;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

public interface IDomain {

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @Override
    String toString();

    @Nullable
    Long getId();

    @Nullable
    Boolean getVisible();


    static boolean domainEquals(@NonNull IDomain domain, @Nullable Object obj) {
        if (domain == obj) {
            return true;
        }
        Class<?> clazz = domain.getClass();
        if (!(clazz.isInstance(obj))) {
            return false;
        }
        IDomain o = (IDomain) obj;
        return ObjectUtils.nullSafeEquals(domain.getId(), o.getId());
    }


    static int domainHash(@NonNull IDomain domain) {
        if (domain.getId() == null) {
            return 0;
        }
        return Objects.hashCode(domain);
    }


}
