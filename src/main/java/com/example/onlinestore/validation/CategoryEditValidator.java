package com.example.onlinestore.validation;

import com.example.onlinestore.domain.models.binding.CategoryEditBindingModel;
import com.example.onlinestore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

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
            errors.rejectValue("name", "Name must contains at least 3 symbols!", "Name must contains at least 3 symbols!");
        }

        this.categoryRepository
                .findByName(categoryEditBindingModel.getName())
                .ifPresent((c) -> errors.rejectValue(
                        "name",
                        String.format("Category with name '%s' already exists!", c.getName()),
                        String.format("Category with name '%s' already exists!", c.getName()))
                );

    }
}
