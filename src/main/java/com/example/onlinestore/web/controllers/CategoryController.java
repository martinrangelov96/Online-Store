package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.models.binding.CategoryAddBindingModel;
import com.example.onlinestore.domain.models.binding.CategoryEditBindingModel;
import com.example.onlinestore.domain.models.service.CategoryServiceModel;
import com.example.onlinestore.domain.models.view.categories.CategoryViewModel;
import com.example.onlinestore.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/categories")
public class CategoryController extends BaseController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/add-category")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addCategory() {
        return view("/categories/add-category");
    }

    @PostMapping("/add-category")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addCategoryConfirm(@ModelAttribute(name = "model") CategoryAddBindingModel model) {
        CategoryServiceModel categoryServiceModel = this.modelMapper.map(model, CategoryServiceModel.class);

        this.categoryService.addCategory(categoryServiceModel);

        return redirect("/categories/all-categories");
    }

    @GetMapping("/all-categories")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
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
    public ModelAndView editCategory(@PathVariable String id, ModelAndView modelAndView) {
        CategoryServiceModel categoryServiceModel = this.categoryService.findCategoryById(id);
        CategoryViewModel categoryViewModel = this.modelMapper.map(categoryServiceModel, CategoryViewModel.class);

        modelAndView.addObject("category", categoryViewModel);

        return view("/categories/edit-category", modelAndView);
    }

    @PatchMapping("/edit-category/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView editCategoryConfirm(@PathVariable String id, @ModelAttribute(name = "model") CategoryEditBindingModel model) {
        CategoryServiceModel categoryServiceModel = this.modelMapper.map(model, CategoryServiceModel.class);

        this.categoryService.editCategory(id, categoryServiceModel);

        return redirect("/categories/all-categories");
    }

    @GetMapping("/delete-category/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
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
