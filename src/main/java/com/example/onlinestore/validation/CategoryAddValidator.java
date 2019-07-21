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
    public void validate(Object o, Errors errors) {
        CategoryAddBindingModel categoryAddBindingModel = (CategoryAddBindingModel) o;

        if (categoryAddBindingModel.getName() == null) {
            errors.rejectValue("name", "Name cannot be null!", "Name cannot be null!");
        }

        if (categoryAddBindingModel.getName().equals("")) {
            errors.rejectValue("name", "Name cannot be empty", "Name cannot be empty!");
        }

        if (categoryAddBindingModel.getName().length() < 2) {
            errors.rejectValue("name", "Name must contains at least 3 symbols!", "Name must contains at least 3 symbols!");
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
