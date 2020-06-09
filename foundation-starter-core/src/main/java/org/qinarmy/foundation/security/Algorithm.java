package org.qinarmy.foundation.security;

public enum Algorithm {
    AES("AES"),

    RSA("RSA"),

    AES_CBC_NO_PADDING("AES/CBC/NoPadding"),

    AES_CBC_PKCS5PADDING("AES/CBC/PKCS5Padding"),

    AES_ECB_NOPADDING("AES/ECB/NoPadding"),

    AES_ECB_PKCS5PADDING("AES/ECB/PKCS5Padding"),


    RSA_ECB_PKCS1PADDING("RSA/ECB/PKCS1Padding"),

    RSA_ECB_OAEPWITHSHA1_ANDMGF1PADDING("RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),

    RSA_ECB_OAEPWITHSHA256_ANDMGF1PADDING("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

    private final String algorithm;

    Algorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
