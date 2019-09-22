package org.qinarmy.foundation.util;



import org.qinarmy.foundation.data.ParamValidateException;
import org.qinarmy.foundation.struct.CodeEnum;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * 这是一个断言工具类,由于扩展于 {@link org.springframework.util.Assert} ,所以可直接使用 spring 的断言工具
 * created  on 03/03/2018.
 */
public abstract class Assert extends org.springframework.util.Assert {

    protected Assert() {

    }

    /**
     * @see String#format(String, Object...)
     */
    public static void notNull(Object object, String format, Object... args) throws IllegalArgumentException {
        if (object == null) {
            throwIllegalArgumentException( format, args );
        }
    }

    /**
     * @see String#format(String, Object...)
     */
    public static void assertNull(Object object, String format, Object... args) throws ParamValidateException {
        if (object != null) {
            throwParamValidateException( format, args );
        }
    }

    /**
     * 此方法与 {@link #notNull(Object, String, Object...)} 不同的是此方法抛出的是 {@link ParamValidateException}
     *
     * @see String#format(String, Object...)
     */
    public static void assertNotNull(Object object, String format, Object... args) throws ParamValidateException {
        if (object == null) {
            throwParamValidateException( format, args );
        }
    }



    public static void isFalse(boolean expression, String format, Object... args) throws IllegalArgumentException {
        if (expression) {
            throwIllegalArgumentException( format, args );
        }
    }

    /**
     * 此方法与 {@link Assert#isFalse(boolean, String, Object...)}  不同的是此方法抛出的是 {@link ParamValidateException}
     *
     * @see String#format(String, Object...)
     */
    public static void assertFalse(boolean expression, String format, Object... args) throws ParamValidateException {
        if (expression) {
            throwParamValidateException( format, args );
        }
    }

    public static void isTrue(boolean expression, String format, Object... args) throws IllegalArgumentException {
        isFalse( !expression, format, args );
    }

    /**
     * 此方法与 {@link Assert#isTrue(boolean, String, Object...)}  不同的是此方法抛出的是 {@link ParamValidateException}
     *
     * @see String#format(String, Object...)
     */
    public static void assertTrue(boolean expression, String format, Object... args) throws ParamValidateException {
        assertFalse( !expression, format, args );
    }

    /**
     * 此方法与 {@link Assert#hasText(String)} 不同的是此方法抛出的是 {@link ParamValidateException}
     */
    public static void assertHasText(String text, String format, Object... args) throws ParamValidateException {
        try {
            hasText( text, format );
        } catch (Exception e) {
            throwParamValidateException( format, args );
        }
    }

    /**
     * 此方法与 {@link Assert#hasLength(String)} 不同的是此方法抛出的是 {@link ParamValidateException}
     */
    public static void assertHasLength(String text, String format, Object... args) throws ParamValidateException {
        try {
            hasLength( text, format );
        } catch (Exception e) {
            throwParamValidateException( format, args );
        }
    }

    /**
     * 若两都 {@code null} 则认为两者不相待
     */
    public static void assertEquals(Object actual, Object expected, String format, Object... args) throws ParamValidateException {
        boolean notEquals = actual == null && expected == null
                || expected != null && !expected.equals( actual )
                || !actual.equals( expected );

        if (notEquals) {
            throwParamValidateException( format, args );
        }

    }

    /**
     * 若两都 {@code null} 则认为两者不相待
     */
    public static void assertNotEquals(Object actual, Object expected, String format, Object... args) throws ParamValidateException {
        boolean equals = expected != null && expected.equals( actual )
                || actual != null && actual.equals( expected );

        if (equals) {
            throwParamValidateException( format, args );
        }
    }

    /**
     * 断言 number 大于 0
     * <p>
     * 目前仅支持
     * <ul>
     * <li>{@link Integer}</li>
     * <li>{@link Long}</li>
     * <li>{@link BigDecimal}</li>
     * <li>{@link Double}</li>
     * <li>{@link BigInteger}</li>
     * <li>{@link Float}</li>
     * <li>{@link Short}</li>
     * <li>{@link Byte}</li>
     * </ul>
     * </p>
     */
    public static void assertGtZero(Number number, String format, Object... args) throws ParamValidateException {
        assertNotNull( number, format, args );

        boolean legal;
        if (number instanceof Integer) {
            legal = ((Integer) number).compareTo( 0 ) > 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Long) {
            legal = ((Long) number).compareTo( 0L ) > 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof BigDecimal) {
            legal = ((BigDecimal) number).compareTo( BigDecimal.ZERO ) > 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Double) {
            legal = ((Double) number).compareTo( 0.00 ) > 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof BigInteger) {
            legal = ((BigInteger) number).compareTo( BigInteger.ZERO ) > 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Float) {
            legal = ((Float) number).compareTo( 0.0F ) > 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Short) {
            legal = ((Short) number).compareTo( (short) 0 ) > 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Byte) {
            legal = ((Byte) number).compareTo( (byte) 0 ) > 0;
            Assert.assertTrue( legal, format, args );
        } else {
            throw new IllegalArgumentException( String.format( "type[%s] not support", number.getClass().getName() ) );
        }
    }


    /**
     * 断言 number 大于等于 0
     * <p>
     * 目前仅支持
     * <ul>
     * <li>{@link Integer}</li>
     * <li>{@link Long}</li>
     * <li>{@link BigDecimal}</li>
     * <li>{@link Double}</li>
     * <li>{@link BigInteger}</li>
     * <li>{@link Float}</li>
     * <li>{@link Short}</li>
     * <li>{@link Byte}</li>
     * </ul>
     * </p>
     */
    public static void assertGeZero(Number number, String format, Object... args) throws ParamValidateException {
        assertNotNull( number, format, args );

        boolean legal;
        if (number instanceof Integer) {
            legal = ((Integer) number).compareTo( 0 ) >= 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Long) {
            legal = ((Long) number).compareTo( 0L ) >= 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof BigDecimal) {
            legal = ((BigDecimal) number).compareTo( BigDecimal.ZERO ) >= 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Double) {
            legal = ((Double) number).compareTo( 0.00 ) >= 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof BigInteger) {
            legal = ((BigInteger) number).compareTo( BigInteger.ZERO ) >= 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Float) {
            legal = ((Float) number).compareTo( 0.0F ) >= 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Short) {
            legal = ((Short) number).compareTo( (short) 0 ) >= 0;
            Assert.assertTrue( legal, format, args );
        } else if (number instanceof Byte) {
            legal = ((Byte) number).compareTo( (byte) 0 ) >= 0;
            Assert.assertTrue( legal, format, args );
        } else {
            throw new IllegalArgumentException( String.format( "type[%s] not support", number.getClass().getName() ) );
        }
    }

    /**
     * 当 number 不为 null 时 断言 number 大于 0
     */
    public static void assertOptionalGt(Number number, String format, Object... args) throws ParamValidateException {
        if (number != null) {
            assertGtZero( number, format, args );
        }
    }

    /**
     * 当 number 不为 null 时 断言 number 大于等于 0
     */
    public static void assertOptionalGeZero(Number number, String format, Object... args) throws ParamValidateException {
        if (number != null) {
            assertGeZero( number, format, args );
        }
    }

    public static void assertOptionalHasText(String text, String format, Object... args) throws ParamValidateException {
        if (text != null) {
            assertHasText( text, format, args );
        }
    }


    public static void assertGt(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws ParamValidateException {
        if (number1.compareTo( number2 ) <= 0) {
            throwParamValidateException( format, args );
        }
    }


    public static void assertGe(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws ParamValidateException {
        if (number1.compareTo( number2 ) < 0) {
            throwParamValidateException( format, args );
        }
    }

    public static void assertLt(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws ParamValidateException {
        if (number1.compareTo( number2 ) >= 0) {
            throwParamValidateException( format, args );
        }
    }


    public static void assertLe(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws ParamValidateException {
        if (number1.compareTo( number2 ) > 0) {
            throwParamValidateException( format, args );
        }
    }

    public static void assertEq(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws ParamValidateException {
        if (number1.compareTo( number2 ) != 0) {
            throwParamValidateException( format, args );
        }
    }


    public static void assertGt(Integer number1, Integer number2, String format, Object... args)
            throws ParamValidateException {
        if (number1.compareTo( number2 ) <= 0) {
            throwParamValidateException( format, args );
        }
    }


    public static void assertGe(Integer number1, Integer number2, String format, Object... args)
            throws ParamValidateException {
        if (number1.compareTo( number2 ) < 0) {
            throwParamValidateException( format, args );
        }
    }

    public static void assertGe(CodeEnum codeEnum1, CodeEnum codeEnum2, String format, Object... args)throws ParamValidateException {
        if (CodeEnum.compare(codeEnum1, codeEnum2).isLt()) {
            throwParamValidateException( format, args );
        }
    }

    public static void assertGt(CodeEnum codeEnum1,CodeEnum codeEnum2, String format, Object... args)throws ParamValidateException {
        if (CodeEnum.compare(codeEnum1, codeEnum2).isLe()) {
            throwParamValidateException( format, args );
        }
    }

    public static void assertLe(CodeEnum codeEnum1,CodeEnum codeEnum2, String format, Object... args)throws ParamValidateException {
        if (CodeEnum.compare(codeEnum1, codeEnum2).isGt()) {
            throwParamValidateException( format, args );
        }
    }

    public static void assertLt(CodeEnum codeEnum1,CodeEnum codeEnum2, String format, Object... args)throws ParamValidateException {
        if (CodeEnum.compare(codeEnum1, codeEnum2).isGe()) {
            throwParamValidateException( format, args );
        }
    }

    /**
     * @throws ParamValidateException 1.date 为 null;2.date 不是未来时间;
     */
    public static void assertFuture(LocalDateTime dateTime, String format, Object... args) throws ParamValidateException {
        if (!dateTime.isAfter( LocalDateTime.now() )) {
            throwParamValidateException( format, args );
        }
    }


    /**
     * @throws ParamValidateException 1.date 为 null;2.date 不是过去时间;
     */
    public static void assertPast(LocalDateTime dateTime, String format, Object... args) throws ParamValidateException {
        if (!dateTime.isBefore( LocalDateTime.now() )) {
            throwParamValidateException( format, args );
        }
    }

    /**
     * @throws ParamValidateException 1.date 为 null;2.date 不是未来时间;
     */
    public static void assertFuture(LocalDate date, String format, Object... args) throws ParamValidateException {
        if (!date.isAfter( LocalDate.now() )) {
            throwParamValidateException( format, args );
        }
    }


    /**
     * @throws ParamValidateException 1.date 为 null;2.date 不是过去时间;
     */
    public static void assertPast(LocalDate date, String format, Object... args) throws ParamValidateException {
        if (!date.isBefore( LocalDate.now() )) {
            throwParamValidateException( format, args );
        }
    }

    public static void assertNotEmpty(Collection<?> collection, String format, Object... args) throws ParamValidateException {
        if (CollectionUtils.isEmpty( collection )) {
            throwParamValidateException( format, args );
        }
    }

    public static void assertNotEmpty(Map<?, ?> map, String format, Object... args) throws ParamValidateException {
        if (CollectionUtils.isEmpty( map )) {
            throwParamValidateException( format, args );
        }
    }





    private static void throwParamValidateException(String format, Object... args) throws ParamValidateException{
        throw new ParamValidateException(null , format,args );
    }

    private static void throwIllegalArgumentException(String format, Object... args) throws IllegalArgumentException{
        String text = args == null ? format : String.format( format, args );
        throw new IllegalArgumentException(text );
    }


}
