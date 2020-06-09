package org.qinarmy.foundation.security;

import org.springframework.lang.NonNull;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

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
        KeySpec spec = createKeySpec(type, getEncoded(base64Text));
        Key key = null;
        if (spec instanceof SecretKeySpec) {
            key = (Key) (spec);
        }
        if (key == null) {
            throw new SecurityKeyException("not support KeyType[%s]", type);
        }

        return key;
    }

    public static PrivateKey readPrivateKey(KeyPairType type, String base64Text) throws SecurityKeyException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(type.algorithm());
            return keyFactory.generatePrivate(
                    createKeySpec(type, true, getEncoded(base64Text))
            );
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityKeyException(e, "algorithm[%s] is supported by current JDK", type.algorithm());
        } catch (InvalidKeySpecException e) {
            throw new SecurityKeyException(e, "key spec error");
        }
    }

    /**
     * @param base64Text see {@link Base64#getMimeEncoder()}
     */
    public static PublicKey readPublicKey(KeyPairType type, String base64Text) throws SecurityKeyException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(type.algorithm());
            return keyFactory.generatePublic(
                    createKeySpec(type, false, getEncoded(base64Text))
            );
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityKeyException(e, "algorithm[%s] is supported by current JDK", type.algorithm());
        } catch (InvalidKeySpecException e) {
            throw new SecurityKeyException(e, "key spec error");
        }
    }

    public static Key createKey(KeyType type, int keySize) throws SecurityKeyException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(type.algorithm());
            keyGenerator.init(keySize, new SecureRandom());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityKeyException(e, "algorithm[%s] is supported by current JDK", type.algorithm());
        } catch (InvalidParameterException e) {
            throw new SecurityKeyException(e, "keySize error.%s", e.getMessage());
        }
    }

    public static KeyPair createKeyPair(KeyPairType type, int keySize) throws SecurityKeyException {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(type.algorithm());
            generator.initialize(keySize, new SecureRandom());
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityKeyException(e, "algorithm[%s] is supported by current JDK", type.algorithm());
        } catch (InvalidParameterException e) {
            throw new SecurityKeyException(e, "keySize error.%s", e.getMessage());
        }
    }

    public static void writeTo(File file, Key key) throws IOException {
        write(new FileWriter(file), key);
    }

    public static void write(Writer w, Key key) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(w)) {

            doWriteToFile(writer, key);

        }
    }

    public static String writeToString(Key key) {
        return Base64.getMimeEncoder().encodeToString(key.getEncoded()).replaceAll("\\r?\\n?", "");
    }


    /*##################### protected ########################*/

    protected static KeySpec createKeySpec(@NonNull KeyType type, byte[] encoded) {
        return new SecretKeySpec(encoded, type.algorithm());
    }

    protected static KeySpec createKeySpec(@NonNull KeyPairType type, boolean privateKey, byte[] encoded) {
        KeySpec spec;

        switch (type) {
            case RSA:
            case DH:
            case EC:
            case DSA:
                spec = privateKey ? new PKCS8EncodedKeySpec(encoded) : new X509EncodedKeySpec(encoded);
                break;
            default:
                throw new IllegalArgumentException(String.format("KeyPairType[%s] unknown", type));
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
            typeDesc = key.getAlgorithm() + " PRIVATE";
        } else if (key instanceof PublicKey) {
            typeDesc = key.getAlgorithm() + " PUBLIC";
        } else {
            typeDesc = key.getAlgorithm();
        }
        return typeDesc;
    }

    private static Base64.Encoder getEncoder() {
        return Base64.getMimeEncoder();
    }


}
