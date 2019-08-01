package com.example.onlinestore.validation;

import com.example.onlinestore.domain.models.binding.CategoryAddBindingModel;
import com.example.onlinestore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.example.onlinestore.constants.Constants.*;

@Component
public class CategoryAddValidator implements Validator {

    private static final String NAME_CONST = "name";

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryAddValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return CategoryAddBindingModel.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CategoryAddBindingModel categoryAddBindingModel = (CategoryAddBindingModel) target;

        if (categoryAddBindingModel.getName().length() < 3) {
            errors.rejectValue(NAME_CONST, NAME_LENGTH_VALIDATION, NAME_MUST_CONTAINS_SYMBOLS_MESSAGE);
        }

        this.categoryRepository
                .findByName(categoryAddBindingModel.getName())
                .ifPresent((c) -> errors.rejectValue(
                        NAME_CONST,
                        String.format(CATEGORY_ALREADY_EXISTS_VALIDATION_MESSAGE, c.getName()),
                        String.format(CATEGORY_ALREADY_EXISTS_VALIDATION_MESSAGE, c.getName()))
                );

    }
}
