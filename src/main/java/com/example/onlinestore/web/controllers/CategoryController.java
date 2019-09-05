package com.example.onlinestore.web.controllers;

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

import static com.example.onlinestore.constants.Constants.*;
import static com.example.onlinestore.constants.CategoryConstants.*;

@Controller
@RequestMapping(REQUEST_MAPPING_CATEGORIES_CONST)
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

    @GetMapping(ADD_CATEGORY_GET)
    @PreAuthorize(HAS_ROLE_MODERATOR)
    @PageTitle(ADD_CATEGORY_PAGE_TITLE)
    public ModelAndView addCategory(@ModelAttribute(name = MODEL_ATTRIBUTE) CategoryAddBindingModel model) {
        return view(ADD_CATEGORY_VIEW_NAME);
    }

    @PostMapping(ADD_CATEGORY_POST)
    @PreAuthorize(HAS_ROLE_MODERATOR)
    public ModelAndView addCategoryConfirm(@ModelAttribute(name = MODEL_ATTRIBUTE) CategoryAddBindingModel model, BindingResult bindingResult) {
        this.categoryAddValidator.validate(model, bindingResult);

        if (bindingResult.hasErrors()) {
            return view(ADD_CATEGORY_VIEW_NAME);
        }

        CategoryServiceModel categoryServiceModel = this.modelMapper.map(model, CategoryServiceModel.class);

        this.categoryService.addCategory(categoryServiceModel);
        return redirect(ALL_CATEGORIES_URL);
    }

    @GetMapping(ALL_CATEGORIES_GET)
    @PreAuthorize(HAS_ROLE_MODERATOR)
    @PageTitle(ALL_CATEGORIES_PAGE_TITLE)
    public ModelAndView allCategories(ModelAndView modelAndView) {
        List<CategoryViewModel> categoryViewModels = this.categoryService.findAllCategories()
                .stream()
                .map(categoryServiceModel -> this.modelMapper.map(categoryServiceModel, CategoryViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject(CATEGORIES_ATTRIBUTE, categoryViewModels);
        return view(ALL_CATEGORIES_VIEW_NAME, modelAndView);
    }

    @GetMapping(EDIT_CATEGORY_BY_ID_GET)
    @PreAuthorize(HAS_ROLE_MODERATOR)
    @PageTitle(EDIT_CATEGORY_PAGE_TITLE)
    public ModelAndView editCategory(@PathVariable String id, ModelAndView modelAndView, CategoryEditBindingModel model) {
        CategoryServiceModel categoryServiceModel = this.categoryService.findCategoryById(id);
        CategoryViewModel categoryViewModel = this.modelMapper.map(categoryServiceModel, CategoryViewModel.class);

        modelAndView.addObject(ID_CONST, categoryViewModel.getId());
        modelAndView.addObject(NAME_CONST, categoryViewModel.getName());
        modelAndView.addObject(MODEL_ATTRIBUTE, model);
        return view(EDIT_CATEGORY_VIEW_NAME, modelAndView);
    }

    @PatchMapping(EDIT_CATEGORY_BY_ID_PATCH)
    @PreAuthorize(HAS_ROLE_MODERATOR)
    public ModelAndView editCategoryConfirm(@PathVariable String id, @ModelAttribute(name = MODEL_ATTRIBUTE) CategoryEditBindingModel model, BindingResult bindingResult, ModelAndView modelAndView) {
        this.categoryEditValidator.validate(model, bindingResult);

        if (bindingResult.hasErrors()) {
            modelAndView.addObject(ID_CONST, id);
            modelAndView.addObject(NAME_CONST, model.getName());
            modelAndView.addObject(MODEL_ATTRIBUTE, model);

            return view(EDIT_CATEGORY_VIEW_NAME, modelAndView);
        }

        CategoryServiceModel categoryServiceModel = this.modelMapper.map(model, CategoryServiceModel.class);

        this.categoryService.editCategory(id, categoryServiceModel);
        return redirect(ALL_CATEGORIES_URL);
    }

    @GetMapping(DELETE_CATEGORY_BY_ID_GET)
    @PreAuthorize(HAS_ROLE_MODERATOR)
    @PageTitle(DELETE_CATEGORY_PAGE_TITLE)
    public ModelAndView deleteCategory(@PathVariable String id, ModelAndView modelAndView) {
        CategoryServiceModel categoryServiceModel = this.categoryService.findCategoryById(id);
        CategoryViewModel categoryViewModel = this.modelMapper.map(categoryServiceModel, CategoryViewModel.class);

        modelAndView.addObject(CATEGORY_ATTRIBUTE, categoryViewModel);
        return view(DELETE_CATEGORY_VIEW_NAME, modelAndView);
    }

    @DeleteMapping(DELETE_CATEGORY_BY_ID_DELETE)
    @PreAuthorize(HAS_ROLE_MODERATOR)
    public ModelAndView deleteCategoryConfirm(@PathVariable String id) {
        this.categoryService.deleteCategory(id);
        return redirect(ALL_CATEGORIES_URL);
    }

    @GetMapping(FETCH_GET)
    @PreAuthorize(HAS_ROLE_MODERATOR)
    @ResponseBody
    public List<CategoryViewModel> fetchCategories() {
        List<CategoryViewModel> categoryViewModels = this.categoryService.findAllCategories()
                .stream()
                .map(categoryServiceModel -> this.modelMapper.map(categoryServiceModel, CategoryViewModel.class))
                .collect(Collectors.toList());

        return categoryViewModels;
    }

}
