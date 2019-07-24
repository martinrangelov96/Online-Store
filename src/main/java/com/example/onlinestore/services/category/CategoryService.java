package com.example.onlinestore.services.category;

import com.example.onlinestore.domain.models.service.CategoryServiceModel;

import java.util.List;

public interface CategoryService {

    CategoryServiceModel addCategory(CategoryServiceModel categoryServiceModel);

    List<CategoryServiceModel> findAllCategories();

    CategoryServiceModel findCategoryById(String id);

    CategoryServiceModel editCategory(String id, CategoryServiceModel categoryServiceModel);

    CategoryServiceModel deleteCategory(String id);
}
