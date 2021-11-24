package com.plango.api.common.component;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeGenerator {

    static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final int size = 12;
    static SecureRandom rnd = new SecureRandom();

    public String randomInvitationCode(){
        StringBuilder code = new StringBuilder(size);
        for(int i = 0; i < size; i++)
            code.append(characters.charAt(rnd.nextInt(characters.length())));
        return code.toString();
    }
}
