package com.example.onlinestore.validation;

import com.example.onlinestore.domain.models.binding.CategoryEditBindingModel;
import com.example.onlinestore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.example.onlinestore.constants.Constants.*;

@Component
public class CategoryEditValidator implements Validator {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryEditValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return CategoryEditBindingModel.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CategoryEditBindingModel categoryEditBindingModel = (CategoryEditBindingModel) target;

        if (categoryEditBindingModel.getName().length() < 3) {
            errors.rejectValue("name", NAME_LENGTH_VALIDATION, NAME_MUST_CONTAINS_SYMBOLS_MESSAGE);
        }

        this.categoryRepository
                .findByName(categoryEditBindingModel.getName())
                .ifPresent((c) -> errors.rejectValue(
                        "name",
                        String.format(CATEGORY_ALREADY_EXISTS_VALIDATION_MESSAGE, c.getName()),
                        String.format(CATEGORY_ALREADY_EXISTS_VALIDATION_MESSAGE, c.getName()))
                );

    }
}
