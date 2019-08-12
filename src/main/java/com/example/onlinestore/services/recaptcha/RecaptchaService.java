package com.example.onlinestore.services.recaptcha;

public interface RecaptchaService {

    String verifyRecaptcha(String userIpAddress, String recaptchaResponse);

}
