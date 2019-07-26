package com.example.onlinestore.validation;

import com.example.onlinestore.domain.models.binding.CategoryAddBindingModel;
import com.example.onlinestore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CategoryAddValidator implements Validator {

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
            errors.rejectValue("name", "Name length validation", "Name must contains at least 3 symbols!");
        }

        this.categoryRepository
                .findByName(categoryAddBindingModel.getName())
                .ifPresent((c) -> errors.rejectValue(
                        "name",
                        String.format("Category with name '%s' already exists!", c.getName()),
                        String.format("Category with name '%s' already exists!", c.getName()))
                );

    }
}
