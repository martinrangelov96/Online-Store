package com.example.onlinestore.web.controllers;

import com.example.onlinestore.constants.Constants;
import com.example.onlinestore.domain.models.binding.CategoryAddBindingModel;
import com.example.onlinestore.domain.models.binding.CategoryEditBindingModel;
import com.example.onlinestore.domain.models.service.CategoryServiceModel;
import com.example.onlinestore.domain.models.view.categories.CategoryViewModel;
import com.example.onlinestore.services.category.CategoryService;
import com.example.onlinestore.validation.CategoryAddValidator;
import com.example.onlinestore.validation.CategoryEditValidator;
import com.example.onlinestore.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.MODEL_NAME;

@Controller
@RequestMapping("/categories")
public class CategoryController extends BaseController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final CategoryAddValidator categoryAddValidator;
    private final CategoryEditValidator categoryEditValidator;

    @Autowired
    public CategoryController(CategoryService categoryService, ModelMapper modelMapper, CategoryAddValidator categoryAddValidator, CategoryEditValidator categoryEditValidator) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
        this.categoryAddValidator = categoryAddValidator;
        this.categoryEditValidator = categoryEditValidator;
    }

    @GetMapping("/add-category")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Add Category")
    public ModelAndView addCategory(@ModelAttribute(name = MODEL_NAME) CategoryAddBindingModel model) {

        return view("/categories/add-category");
    }

    @PostMapping("/add-category")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addCategoryConfirm(@ModelAttribute(name = MODEL_NAME) CategoryAddBindingModel model, BindingResult bindingResult) {
        this.categoryAddValidator.validate(model, bindingResult);

        if (bindingResult.hasErrors()) {
            return view("/categories/add-category");
        }

        CategoryServiceModel categoryServiceModel = this.modelMapper.map(model, CategoryServiceModel.class);
        this.categoryService.addCategory(categoryServiceModel);

        return redirect("/categories/all-categories");
    }

    @GetMapping("/all-categories")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("All Categories")
    public ModelAndView allCategories(ModelAndView modelAndView) {
        List<CategoryViewModel> categoryViewModels = this.categoryService.findAllCategories()
                .stream()
                .map(categoryServiceModel -> this.modelMapper.map(categoryServiceModel, CategoryViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("categories", categoryViewModels);

        return view("/categories/all-categories", modelAndView);
    }

    @GetMapping("/edit-category/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Edit Category")
    public ModelAndView editCategory(@PathVariable String id, ModelAndView modelAndView, CategoryEditBindingModel model) {
        CategoryServiceModel categoryServiceModel = this.categoryService.findCategoryById(id);
        CategoryViewModel categoryViewModel = this.modelMapper.map(categoryServiceModel, CategoryViewModel.class);

        modelAndView.addObject("id", categoryViewModel.getId());
        modelAndView.addObject("name", categoryViewModel.getName());
        modelAndView.addObject(MODEL_NAME, model);

        return view("/categories/edit-category", modelAndView);
    }

    @PatchMapping("/edit-category/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView editCategoryConfirm(@PathVariable String id, @ModelAttribute(name = MODEL_NAME) CategoryEditBindingModel model, BindingResult bindingResult, ModelAndView modelAndView) {
        this.categoryEditValidator.validate(model, bindingResult);

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("id", id);
            modelAndView.addObject("name", model.getName());
            modelAndView.addObject(MODEL_NAME, model);

            return view("/categories/edit-category", modelAndView);
        }

        CategoryServiceModel categoryServiceModel = this.modelMapper.map(model, CategoryServiceModel.class);
        this.categoryService.editCategory(id, categoryServiceModel);

        return redirect("/categories/all-categories");
    }

    @GetMapping("/delete-category/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Delete Category")
    public ModelAndView deleteCategory(@PathVariable String id, ModelAndView modelAndView) {
        CategoryServiceModel categoryServiceModel = this.categoryService.findCategoryById(id);
        CategoryViewModel categoryViewModel = this.modelMapper.map(categoryServiceModel, CategoryViewModel.class);

        modelAndView.addObject("category", categoryViewModel);

        return view("/categories/delete-category", modelAndView);
    }

    @DeleteMapping("/delete-category/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteCategoryConfirm(@PathVariable String id) {
        this.categoryService.deleteCategory(id);

        return redirect("/categories/all-categories");
    }

    @GetMapping("/fetch")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @ResponseBody
    public List<CategoryViewModel> fetchCategories() {
        List<CategoryViewModel> categoryViewModels = this.categoryService.findAllCategories()
                .stream()
                .map(categoryServiceModel -> this.modelMapper.map(categoryServiceModel, CategoryViewModel.class))
                .collect(Collectors.toList());

        return categoryViewModels;
    }

}
