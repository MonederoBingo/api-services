package com.lealpoints.service.util;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtil {
    public String generateActivationKey(){
        return RandomStringUtils.random(60, true, true);
    }

    public String generatePassword() {
        return RandomStringUtils.random(6, true, true);
    }
}
