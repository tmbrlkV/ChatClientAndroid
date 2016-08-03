package com.chat_client.database.util.security;

import android.support.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {
    @NonNull
    public static String hash(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(string.getBytes());
            byte[] passwordDigest = messageDigest.digest();
            StringBuilder hex = new StringBuilder();
            for (byte digest : passwordDigest) {
                hex.append(Integer.toHexString(0xFF & digest));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
