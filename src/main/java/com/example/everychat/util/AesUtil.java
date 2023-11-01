package com.example.everychat.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesUtil {
    private static String specName = "AES/CBC/PKCS5Padding";
    private static String privateKey = "ahnyutaeprivatek";

    public static String encrypt(String plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(privateKey.getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(privateKey.substring(0,16).getBytes());
        Cipher cipher = Cipher.getInstance(specName);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(encrypted));
    }

    public static String decrypt(String cipherText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(privateKey.getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(privateKey.substring(0,16).getBytes());
        Cipher cipher = Cipher.getInstance(specName);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
