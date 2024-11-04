package com.example.echoling.auntefication;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;


@Component
public class CookieUtils {

    private SessionDataSaver sessionDataSaver;
    public CookieUtils(SessionDataSaver sessionDataSaver){
        this.sessionDataSaver = sessionDataSaver;
    }

    public void createSecureCookie(HttpServletResponse response, String name, String value, int maxAge) throws Exception {
        createCookie(response, name, Encryption.encryptWithAES(value, sessionDataSaver.getCode()), maxAge);
    }

    public Optional<String> getDecryptedCookie(HttpServletRequest request, String name) throws Exception {
        var cookie = getCookie(request, name);
        return cookie.map(s -> Encryption.decryptWithAES(s, sessionDataSaver.getCode()));
    }
    public void createCookie(HttpServletResponse response, String name, String value, int maxAge) throws Exception {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        response.setHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Lax",
                name, value, maxAge));

        response.addCookie(cookie);
    }

    public Optional<String> getCookie(HttpServletRequest request, String name) throws Exception {
        if (request.getCookies() != null) {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals(name))
                    .findFirst();
            if (cookie.isPresent()) {
                return Optional.of(cookie.get().getValue());
            }
        }
        return Optional.empty();
    }
}
