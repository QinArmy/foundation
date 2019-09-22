package org.qinarmy.foundation.struct;

import org.springframework.lang.NonNull;

/**
 * created  on 2019-02-23.
 */
public interface Compare<T> {


    /**
     * @see CompareResult#resolve(int)
     */
    @NonNull
    CompareResult compare(@NonNull T o);


    interface Comparer {

        boolean isEq();

        boolean isLt();

        boolean isLe();

        boolean isGt();

        boolean isGe();

    }

}
