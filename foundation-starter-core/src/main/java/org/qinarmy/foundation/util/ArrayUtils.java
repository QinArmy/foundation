package org.qinarmy.foundation.util;

import org.springframework.lang.NonNull;

import java.time.*;
import java.util.*;

/**
 * created  on 2019-03-13.
 */
public abstract class ArrayUtils {

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static final Integer[] EMPTY_INTEGER_ARRAY = new Integer[0];

    public static final Long[] EMPTY_LONG_ARRAY = new Long[0];

    public static final int[] EMPTY_INT_ARRAY = new int[0];

    public static final LocalTime[] EMPTY_TIME = new LocalTime[0];

    public static final LocalDate[] EMPTY_DATE = new LocalDate[0];

    public static final YearMonth[] EMPTY_YEAR_MONTH = new YearMonth[0];

    public static final MonthDay[] EMPTY_MONTH_DAY = new MonthDay[0];

    public static final LocalDateTime[] EMPTY_DATE_TIME = new LocalDateTime[0];

    public static final ZonedDateTime[] EMPTY_ZONE_DATE_TIME = new ZonedDateTime[0];


    @NonNull
    public static <T> Set<T> asSet(@NonNull Collection<T> collection, T... e) {
        Set<T> set = new HashSet<>( collection );
        if (e != null) {
            Collections.addAll( set, e );
        }
        return set;
    }

    @NonNull
    public static <T> Set<T> asSet(T... e) {
        return asSet( Collections.emptySet(), e );
    }


    @NonNull
    public static <T> Set<T> asUnmodifiableSet(@NonNull Collection<T> collection, T... e) {
        return Collections.unmodifiableSet( asSet( collection, e ) );
    }

    @NonNull
    public static <T> Set<T> asUnmodifiableSet(T... e) {
        return asUnmodifiableSet( Collections.emptySet(), e );
    }

    @NonNull
    public static <T> List<T> asList(@NonNull Collection<T> collection, T... e) {
        List<T> list;
        int size = collection.size();
        if (e != null) {
            size += e.length;
        }
        list = new ArrayList<>( size );
        list.addAll( collection );

        if (e != null) {
            Collections.addAll( list, e );
        }
        return list;
    }


    @NonNull
    public static <T> List<T> asUnmodifiableList(T... e) {
        return Collections.unmodifiableList( asList( Collections.emptyList(), e ) );
    }
}
