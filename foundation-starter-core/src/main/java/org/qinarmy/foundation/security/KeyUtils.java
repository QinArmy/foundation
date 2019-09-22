package org.qinarmy.foundation.security;

import org.springframework.lang.NonNull;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

import static org.qinarmy.foundation.core.ResultCode.KEY_ERROR;

/**
 * created  on 2019-03-13.
 *
 * @see KeyType
 */
public abstract class KeyUtils {


    protected static final String KEY_BEGIN = "-----BEGIN %s %s KEY-----";

    protected static final String KEY_END = "-----END %s %s KEY-----";

    private static final Pattern BOUNDARY_PATTERN = Pattern.compile("(?:^\\s*-+[^-]+-+\\s*(?=[^-])|(?<=[^-])\\s*-+[^-]+-+\\s*$)");


    @NonNull
    public static Key readKey(@NonNull KeyType type, @NonNull String base64Text) throws SecurityKeyException {
        KeySpec spec = getKeySpec(type, getEncoded(base64Text));
        Key key = null;
        if (type == KeyType.AES) {
            key = (Key) (spec);
        }
        return key;
    }

    public static PrivateKey readPrivateKey(KeyPairType type, String base64Text) throws SecurityKeyException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(type.display());
            return keyFactory.generatePrivate(
                    getKeySpec(type, true, getEncoded(base64Text))
            );
        } catch (Exception e) {
            throw new SecurityKeyException(KEY_ERROR, e.getMessage());
        }
    }

    public static PublicKey readPublicKey(KeyPairType type, String base64Text) throws SecurityKeyException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(type.display());
            return keyFactory.generatePublic(
                    getKeySpec(type, false, getEncoded(base64Text))
            );
        } catch (Exception e) {
            throw new SecurityKeyException(KEY_ERROR, e.getMessage());
        }
    }

    public static Key createKey(KeyType type, int keySize) throws SecurityKeyException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(type.display());
            keyGenerator.init(keySize);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new SecurityKeyException(KEY_ERROR, e.getMessage());
        }
    }

    public static KeyPair createKeyPair(KeyPairType type, int keySize) throws SecurityKeyException {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(type.display());
            generator.initialize(keySize);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new SecurityKeyException(KEY_ERROR, e.getMessage());
        }
    }

    public static void writeTo(File file, Key key) throws SecurityKeyException {

        try (FileWriter w = new FileWriter(file); BufferedWriter writer = new BufferedWriter(w)) {

            doWriteToFile(writer, key);

        } catch (IOException e) {
            throw new SecurityKeyException(KEY_ERROR, e.getMessage());
        }
    }

    public static String writeToString(Key key) {
        return Base64.getMimeEncoder().encodeToString(key.getEncoded()).replaceAll("\\r?\\n?", "");
    }


    /*##################### protected ########################*/

    protected static KeySpec getKeySpec(@NonNull KeyType type, byte[] encoded) {
        KeySpec spec;

        switch (type) {
            case AES:
                spec = new SecretKeySpec(encoded, type.display());
                break;
            default:
                throw new IllegalArgumentException(String.format("KeyAlgorithm[%s] unknown", type));
        }
        return spec;
    }

    protected static KeySpec getKeySpec(@NonNull KeyPairType type, boolean privateKey, byte[] encoded) {
        KeySpec spec;

        switch (type) {
            case RSA:
                spec = privateKey ? new PKCS8EncodedKeySpec(encoded) : new X509EncodedKeySpec(encoded);
                break;
            default:
                throw new IllegalArgumentException(String.format("KeyAlgorithm[%s] unknown", type));
        }
        return spec;
    }


    protected static byte[] getEncoded(@NonNull String base64Text) {
        return Base64.getMimeDecoder().decode(
                BOUNDARY_PATTERN.matcher(base64Text).replaceAll("")
        );
    }


    /*##################### private ########################*/

    private static void doWriteToFile(BufferedWriter writer, Key key)
            throws IOException {

        final String base64 = getEncoder().encodeToString(key.getEncoded()).replaceAll("\\r?\\n?", "");
        final String typeDesc = getTypeDesc(key);
        //write top boundary
        writer.write(String.format(KEY_BEGIN, key.getAlgorithm(), typeDesc));
        writer.newLine();

        final int bit = 6, size = 1 << bit;
        int start, end;
        int count = base64.length() / size;
        count = base64.length() % size == 0 ? count : count + 1;

        for (int i = 0; i < count; i++) {
            // start and end
            start = i << bit;
            end = start + size;
            end = Math.min(end, base64.length());
            //write a line
            writer.write(base64.substring(start, end));
            writer.newLine();
        }
        //write bottom boundary
        writer.write(String.format(KEY_END, key.getAlgorithm(), typeDesc));
    }

    private static String getTypeDesc(Key key) {
        String typeDesc;
        if (key instanceof PrivateKey) {
            typeDesc = "PRIVATE";
        } else if (key instanceof PublicKey) {
            typeDesc = "PUBLIC";
        } else {
            typeDesc = "";
        }
        return typeDesc;
    }

    private static Base64.Encoder getEncoder() {
        return Base64.getMimeEncoder();
    }


}
