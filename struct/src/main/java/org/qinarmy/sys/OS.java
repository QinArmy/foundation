package org.qinarmy.sys;

import org.qinarmy.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/10/20.
 */
public enum OS implements CodeEnum {

    LINUX( 0, "linux" ),

    MAC( 100, "mac" ),

    WINDOWS( 200, "windows" ),

    SOLARIS(300,"solaris")

    ;

    private static final Map<Integer, OS> CODE_MAP = CodeEnum.getCodeMap( OS.class );

    public static OS resolve(int code) {
        return CODE_MAP.get( code );
    }


    private final int code;

    private final String display;

    OS(int code, String display) {
        this.code = code;
        this.display = display;
    }



    @Override
    public int code() {
        return code;
    }

    @Override
    public String display() {
        return display;
    }

    /**
     * 获取操作系统类型.
     * 目前 java jdk 的版本有 四种.
     */
    public static OS localOs() {
        final String osName = System.getProperty( "os.name" ).toLowerCase();
        OS os;
        if (osName.contains( LINUX.display() )) {
            os = LINUX;
        } else if (osName.contains( MAC.display() )) {
            os = MAC;
        } else if (osName.contains( WINDOWS.display() )) {
            os = WINDOWS;
        } else if (osName.contains( SOLARIS.display() )) {
            os = SOLARIS;
        } else {
            throw new RuntimeException( String.format( "unknown os %s", osName ) );
        }
        return os;

    }
}
