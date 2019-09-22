package org.qinarmy.foundation.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * created  on 07/03/2018.
 */
public abstract class ReflectionUtils extends org.springframework.util.ReflectionUtils {

    protected ReflectionUtils() {

    }

    /**
     * @return public 修饰的 setter 方法则返回 true
     */
    public static boolean isSetter(Method method) {
        String name = method.getName();
        return Modifier.isPublic( method.getModifiers() )
                && !Modifier.isStatic( method.getModifiers() )
                && name.startsWith( "set" )
                && name.length() > 3
                && Character.isUpperCase( name.charAt( 3 ) )
                && method.getParameterTypes().length == 1;
    }

    /**
     * @return public 修饰的 getter 方法则返回 true
     */
    public static boolean isGetter(Method method) {
        String name = method.getName();
        return Modifier.isPublic( method.getModifiers() )
                && !Modifier.isStatic( method.getModifiers() )
                && name.startsWith( "get" )
                && !name.equals( "getClass" )
                && name.length() > 3
                && Character.isUpperCase( name.charAt( 3 ) )
                && method.getParameterTypes().length == 0;
    }


   public static boolean isPublicStatic(Method method){
        return Modifier.isPublic( method.getModifiers() )
                && Modifier.isStatic( method.getModifiers() );
   }




    /**
     * 从方法名获取属性名
     *
     * @param methodName 方法名(getter|setter) (not null)
     * @return 属性名
     * @throws IllegalArgumentException - methodName 为 null,或长度小于 4
     */
    public static String propertyNameFromMethod(String methodName) throws IllegalArgumentException {
        if (methodName == null || methodName.length() < 4) {
            throw new IllegalArgumentException( String.format( "methodName[%s] Illegal", methodName ) );
        }
        if (methodName.length() < 5) {
            return methodName.substring( 3 ).toLowerCase();
        } else {
            return methodName.substring( 3, 4 ).toLowerCase() + methodName.substring( 4 );
        }

    }


}
