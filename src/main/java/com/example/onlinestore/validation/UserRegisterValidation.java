package com.example.onlinestore.validation;

import com.example.onlinestore.domain.models.binding.UserRegisterBindingModel;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserRegisterValidation implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegisterBindingModel.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegisterBindingModel userRegisterBindingModel = (UserRegisterBindingModel) target;

        if (!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) {
            errors.rejectValue("password", "Passwords don't match!", "Passwords don't match!");
        }
    }
}
