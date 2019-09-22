package org.qinarmy.foundation.orm;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

public interface IVersionDomain extends IDomain {

    @Nullable
    Integer getVersion();


    static boolean versionDomainEquals(@NonNull IVersionDomain domain, @Nullable Object obj) {
        if (domain == obj) {
            return true;
        }
        Class<?> clazz = domain.getClass();
        if (!(clazz.isInstance(obj))) {
            return false;
        }
        IVersionDomain o = (IVersionDomain) obj;
        return ObjectUtils.nullSafeEquals(domain.getId(), o.getId())
                && ObjectUtils.nullSafeEquals(domain.getVersion(), o.getVersion())
                ;
    }

    static int versionDomainHash(@NonNull IVersionDomain domain) {
        if (domain.getId() == null) {
            return 0;
        }
        return Objects.hash(domain.getId(), domain.getVersion());
    }
}
