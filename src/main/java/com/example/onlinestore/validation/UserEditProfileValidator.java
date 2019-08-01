package com.example.onlinestore.validation;

import com.example.onlinestore.domain.entities.User;
import com.example.onlinestore.domain.models.binding.UserEditBindingModel;
import com.example.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.example.onlinestore.constants.Constants.*;

@Component
public class UserEditProfileValidator implements Validator {

    private static final String OLD_PASSWORD = "oldPassword";
    private static final String PASSWORD = "password";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserEditProfileValidator(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserEditBindingModel.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserEditBindingModel userEditBindingModel = (UserEditBindingModel) target;
        User user = this.userRepository.findByUsername(userEditBindingModel.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE));

        if (!this.bCryptPasswordEncoder.matches(userEditBindingModel.getOldPassword(), user.getPassword())) {
            errors.rejectValue(OLD_PASSWORD, INCORRECT_OLD_PASSWORD_VALIDATION_CODE, INCORRECT_OLD_PASSWORD_VALIDATION_MESSAGE);
        }

        if (userEditBindingModel.getPassword() != null && !userEditBindingModel.getPassword().equals(userEditBindingModel.getConfirmPassword())) {
            errors.rejectValue(PASSWORD, INCORRECT_PASSWORD_VALIDATION_CODE, INCORRECT_PASSWORD_VALIDATION_MESSAGE);
        }

    }
}
