package com.example.echoling.auntefication;

import java.util.Random;

public class SessionDataSaver {
    private final String code;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();
    public SessionDataSaver (){
        code = generateRandomString(64);
    }

    public static String generateRandomString(int length) {
        //ONLY FOR TEST
        return "if";
//        StringBuilder sb = new StringBuilder(length);
//        for (int i = 0; i < length; i++) {
//            int index = RANDOM.nextInt(CHARACTERS.length());
//            sb.append(CHARACTERS.charAt(index));
//        }
//        return sb.toString();
    }

    public String getCode() {
        return code;
    }
}
