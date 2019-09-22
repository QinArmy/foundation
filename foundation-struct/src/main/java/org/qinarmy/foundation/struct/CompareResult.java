package org.qinarmy.foundation.struct;

import org.springframework.lang.NonNull;

/**
 * replace {@link Comparable#compareTo(Object)} return value
 * created  on 2019-02-23.
 *
 * @see Comparable
 */
public enum CompareResult implements Compare.Comparer, CodeEnum {

    EQUAL(0, "equals") {
        @Override
        public boolean isEq() {
            return true;
        }

        @Override
        public boolean isLt() {
            return false;
        }

        @Override
        public boolean isLe() {
            return true;
        }

        @Override
        public boolean isGt() {
            return false;
        }

        @Override
        public boolean isGe() {
            return true;
        }
    },
    LESS(-1, "less than") {
        @Override
        public boolean isEq() {
            return false;
        }

        @Override
        public boolean isLt() {
            return true;
        }

        @Override
        public boolean isLe() {
            return true;
        }

        @Override
        public boolean isGt() {
            return false;
        }

        @Override
        public boolean isGe() {
            return false;
        }
    },
    GREAT(1, "great than") {
        @Override
        public boolean isEq() {
            return false;
        }

        @Override
        public boolean isLt() {
            return false;
        }

        @Override
        public boolean isLe() {
            return false;
        }

        @Override
        public boolean isGt() {
            return true;
        }

        @Override
        public boolean isGe() {
            return true;
        }
    };


    /**
     * @see Comparable#compareTo(Object)
     */
    public static CompareResult resolve(int compareResult) {
        CompareResult r;
        if (compareResult == 0) {
            r = CompareResult.EQUAL;
        } else if (compareResult > 0) {
            r = CompareResult.GREAT;
        } else {
            r = CompareResult.LESS;
        }
        return r;
    }


    private final int code;

    private final String display;

    CompareResult(int code, String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public int code() {
        return code;
    }

    @NonNull
    @Override
    public String display() {
        return display;
    }


}
