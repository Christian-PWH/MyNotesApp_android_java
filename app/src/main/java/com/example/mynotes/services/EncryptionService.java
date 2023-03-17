package com.example.mynotes.services;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionService {

    public static String encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        // Return Cipher Text
        return Base64.encodeToString(cipherText, Base64.NO_WRAP);
    }
    public static String decryptMsg(String cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        byte[] decode = Base64.decode(cipherText, Base64.NO_WRAP);
        // Return original text
        return new String(cipher.doFinal(decode), StandardCharsets.UTF_8);
    }

    public static SecretKey generateKey(String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        byte[] saltEnc = salt.getBytes();
        int iterationEnc = 100;
        SecretKeyFactory factoryKeyEnc = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey tmp = factoryKeyEnc.generateSecret(new PBEKeySpec(
                "my password".toCharArray(),
                saltEnc,
                iterationEnc,
                128));
        SecretKeySpec secret;
//        secret = new SecretKeySpec(salt.getBytes(), "AES");
        secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        Log.d("secret key enc service", Arrays.toString(secret.getEncoded()));
        return secret;
    }
}
// https://medium.com/@dev.jeevanyohan/basic-encryption-decryption-in-android-aes-72fd3f06ab4c
// https://stackoverflow.com/questions/10759392/java-aes-encryption-and-decryption
