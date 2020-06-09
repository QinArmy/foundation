package org.qinarmy.foundation.security;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public abstract class CipherUtils {

    protected CipherUtils() {
        throw new UnsupportedOperationException();
    }

    public static String decryptText(Key key, String cipherText) throws Exception {
        byte[] decryptByte = decrypt(key, Base64.getMimeDecoder().decode(cipherText));
        return new String(decryptByte, StandardCharsets.UTF_8);
    }

    public static byte[] decrypt(Key key, byte[] cipherBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(cipherBytes);
    }

    public static byte[] encrypt(Key key, byte[] plantBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plantBytes);
    }

    public static String encryptText(Key key, String plantText) throws Exception {
        byte[] encryptBytes = encrypt(key, plantText.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getMimeEncoder().encode(encryptBytes), StandardCharsets.UTF_8);
    }


}
