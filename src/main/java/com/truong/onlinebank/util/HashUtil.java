package com.truong.onlinebank.util;

public class HashUtil {
    public static String hashPin(String pin) {
        try{
            java.security.MessageDigest md =
                    java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pin.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        } catch (Exception e){
            throw new RuntimeException("Hashing error" , e);
        }
    }
}
