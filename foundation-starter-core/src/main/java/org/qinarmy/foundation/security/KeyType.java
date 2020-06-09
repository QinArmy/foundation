package org.qinarmy.foundation.security;


/**
 * @see KeyUtils
 * @see SignatureUtils
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyGenerator">KeyGenerator Algorithms</a>
 */
public enum KeyType {

    /**
     *
     */
    AES("AES"),
    ARCFOUR("ARCFOUR"),
    BLOWFISH("Blowfish"),
    DES("DES"),

    DE_SEDE("DESede"),
    HMAC_MD5("HmacMD5"),
    HMAC_SHA1("HmacSHA1"),
    HMAC_SHA224("HmacSHA224"),

    HMAC_SHA256("HmacSHA256"),
    HMAC_SHA384("HmacSHA384"),
    HMAC_SHA512("HmacSHA512"),
    RC2("RC2");


    private final String algorithm;


    KeyType(String algorithm) {
        this.algorithm = algorithm;
    }

    public String algorithm() {
        return algorithm;
    }

}
