package com.lealpoints.service.util;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ServiceUtil {
    private final Random random = new Random();

    public String generateActivationKey(){
        return RandomStringUtils.random(60, true, true);
    }

    public String setPassword() {
        String password = "";
        for (int i = 1; i <= 6; i++)
            password += getCharacterOrNumber();
        return password;
    }

    private char getCharacterOrNumber() {
        if (random.nextInt(2) == 1)
            return (char) randomNumber(97, 122);
        else
            return (char) randomNumber(48, 58);
    }

    private int randomNumber(int num1, int num2) {
        return (int) Math.floor(Math.random() * (num1 - num2) + num2);
    }
}
