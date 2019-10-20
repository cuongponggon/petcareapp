package com.project.petcareapp.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.servlet.http.HttpServletRequest;

import static com.project.petcareapp.constants.SecurityConstant.*;

public class Ultilities {

    public static String getUsername(HttpServletRequest request) {
        System.out.println("HEADER IS:" + request.getHeader(HEADER_STRING));
        String tokenHeader = request.getHeader(HEADER_STRING);
        String username = "";
        try {
            if (tokenHeader != null) {
                String token = tokenHeader.replace(TOKEN_PREFIX, "");
                username = getDecodedJWT(token).getSubject();
            }
        } catch (Exception e) {
            username = "";
        }
        return username;
    }

    public static String getUsername1(String tokenHeader) {
        String username = "";
        try {
            if (tokenHeader != null) {
                String token = tokenHeader.replace(TOKEN_PREFIX, "");
                username = getDecodedJWT(token).getSubject();
            }
        } catch (Exception e) {
            username = "";
        }
        return username;
    }

    private static DecodedJWT getDecodedJWT(String token) {
        return JWT.require(Algorithm.HMAC512(JWT_SECRET.getBytes()))
                .build()
                .verify(token);
    }


}
