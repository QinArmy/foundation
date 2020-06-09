package org.qinarmy.foundation.security;


/**
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator">KeyPairGenerator Algorithms</a>
 */
public enum KeyPairType {

    /**
     * Generates keypairs for the RSA algorithm (Signature/Cipher).
     */
    RSA("RSA"),

    /**
     * Generates keypairs for the Diffie-Hellman KeyAgreement algorithm.
     * Note: key.getAlgorithm() will return "DH" instead of "DiffieHellman".
     */
    DH("DiffieHellman"),

    /**
     * Generates keypairs for the Digital Signature Algorithm.
     */
    DSA("DSA"),

//    /**
//     * Generates keypairs for the RSASSA-PSS signature algorithm.
//     */
    // RSASSA_PSS("RSASSA-PSS"),

    /**
     * Generates keypairs for the Elliptic Curve algorithm.
     */
    EC("EC");


    private final String algorithm;


    KeyPairType(String algorithm) {
        this.algorithm = algorithm;
    }

    public String algorithm() {
        return this.algorithm;
    }
}
