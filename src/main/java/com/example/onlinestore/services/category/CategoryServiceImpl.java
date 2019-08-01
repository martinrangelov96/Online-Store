package com.example.onlinestore.services.category;

import com.example.onlinestore.constants.Constants;
import com.example.onlinestore.domain.entities.Category;
import com.example.onlinestore.domain.models.service.CategoryServiceModel;
import com.example.onlinestore.errors.CategoryNotFoundException;
import com.example.onlinestore.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryServiceModel addCategory(CategoryServiceModel categoryServiceModel) {
        Category category = this.modelMapper.map(categoryServiceModel, Category.class);

        this.categoryRepository.save(category);

        return this.modelMapper.map(category, CategoryServiceModel.class);
    }

    @Override
    public List<CategoryServiceModel> findAllCategories() {
        List<CategoryServiceModel> categoryServiceModels = this.categoryRepository.findAll()
                .stream()
                .map(category -> this.modelMapper.map(category, CategoryServiceModel.class))
                .collect(Collectors.toList());

        return categoryServiceModels;
    }

    @Override
    public CategoryServiceModel findCategoryById(String id) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE));

        CategoryServiceModel categoryServiceModel = this.modelMapper.map(category, CategoryServiceModel.class);

        return categoryServiceModel;
    }

    @Override
    public CategoryServiceModel editCategory(String id, CategoryServiceModel categoryServiceModel) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE));

        category.setName(categoryServiceModel.getName());

        this.categoryRepository.save(category);

        return this.modelMapper.map(category, CategoryServiceModel.class);
    }

    @Override
    public CategoryServiceModel deleteCategory(String id) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE));

        this.categoryRepository.delete(category);

        return this.modelMapper.map(category, CategoryServiceModel.class);
    }
}
