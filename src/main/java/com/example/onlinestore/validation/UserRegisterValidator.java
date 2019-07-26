package com.example.onlinestore.validation;

import com.example.onlinestore.constants.Constants;
import com.example.onlinestore.domain.entities.User;
import com.example.onlinestore.domain.models.binding.UserRegisterBindingModel;
import com.example.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.example.onlinestore.constants.Constants.USERNAME_NOT_FOUND_EXCEPTION_MESSAGE;

@Component
public class UserRegisterValidator implements Validator {

    private final UserRepository userRepository;

    @Autowired
    public UserRegisterValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegisterBindingModel.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegisterBindingModel userRegisterBindingModel = (UserRegisterBindingModel) target;
        if (this.userRepository.findByUsername(userRegisterBindingModel.getUsername()).isEmpty()) {
            return;
        }

        User user = this.userRepository.findByUsername(userRegisterBindingModel.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE));

        if (!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) {
            errors.rejectValue("password", "Passwords not matching", "Passwords don't match!");
        }

        if (userRegisterBindingModel.getUsername().equals(user.getUsername())) {
            errors.rejectValue("username", "Duplicate user", "User with this username already exists!" );
        }

//        if (userRegisterBindingModel.getEmail().equals(user.getEmail())) {
//            errors.rejectValue("email", "Duplicate email", "This email is already used by other user!" );
//        }
    }
}
