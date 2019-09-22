package org.qinarmy.foundation.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * created  on 30/03/2018.
 *
 * @see KeyUtils
 */
public abstract class SignatureUtils {

    private static final Logger LOG = LoggerFactory.getLogger( SignatureUtils.class );




    public static String signature(SignatureType type, String content, PrivateKey key) throws Exception{
        return signature( type, content.getBytes( StandardCharsets.UTF_8 ), key );
    }

    public static String signature(SignatureType type, byte[] contentBytes, PrivateKey key)throws Exception {
        Signature signatureObj = Signature.getInstance( type.display() );
        signatureObj.initSign( key );
        signatureObj.update( contentBytes );
        return  Base64.getMimeEncoder().encodeToString( signatureObj.sign() );

    }


    public static boolean verifySignature(SignatureType name, String content, String signature, PublicKey publicKey)
            throws Exception {
        return verifySignature( name, content.getBytes( StandardCharsets.UTF_8 ), signature, publicKey );
    }


    public static boolean verifySignature(SignatureType name, byte[] contentBytes, String signature, PublicKey publicKey)
            throws Exception {
        Signature signatureObj = Signature.getInstance( name.name() );
        signatureObj.initVerify( publicKey );
        signatureObj.update( contentBytes );
        return signatureObj.verify( Base64.getMimeDecoder().decode(signature.getBytes(StandardCharsets.UTF_8)) );
    }
}
