package com.example.onlinestore.services.recaptcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.example.onlinestore.constants.Constants.SUCCESS_STRING;

@Service
public class RecaptchaServiceImpl implements RecaptchaService {

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Value("${google.recaptcha.secret-key}")
    private String recaptchaSecretKey;

    private final RestTemplateBuilder restTemplateBuilder;

    @Autowired
    public RecaptchaServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Override
    public String verifyRecaptcha(String userIpAddress, String recaptchaResponse) {
        Map<String, String> body = new HashMap<>() {{
            put("secret", recaptchaSecretKey);
            put("response", recaptchaResponse);
            put("remoteip", userIpAddress);
        }};

        ResponseEntity<Map> recaptchaResponseEntity =
                restTemplateBuilder.build()
                .postForEntity(GOOGLE_RECAPTCHA_VERIFY_URL+"?secret={secret}&response={response}&remoteip={remoteip}",
                        body, Map.class, body);

        Map<String, Object> responseBody = recaptchaResponseEntity.getBody();

        if ((Boolean) responseBody.get(SUCCESS_STRING)) {
            return SUCCESS_STRING;
        } else {
            return null;
        }

    }
}
