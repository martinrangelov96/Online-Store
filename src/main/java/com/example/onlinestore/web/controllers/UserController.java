package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.models.binding.UserEditBindingModel;
import com.example.onlinestore.domain.models.binding.UserRegisterBindingModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.view.categories.CategoryViewModel;
import com.example.onlinestore.domain.models.view.users.UserProfileViewModel;
import com.example.onlinestore.domain.models.view.users.UserViewModel;
import com.example.onlinestore.services.category.CategoryService;
import com.example.onlinestore.services.cloudinary.CloudinaryService;
import com.example.onlinestore.services.user.UserService;
import com.example.onlinestore.validation.UserEditProfileValidation;
import com.example.onlinestore.validation.UserRegisterValidation;
import com.example.onlinestore.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.*;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper modelMapper;
    private final UserEditProfileValidation userEditProfileValidation;
    private final UserRegisterValidation userRegisterValidation;

    @Autowired
    public UserController(UserService userService, CategoryService categoryService, CloudinaryService cloudinaryService, ModelMapper modelMapper, UserEditProfileValidation userEditProfileValidation, UserRegisterValidation userRegisterValidation) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.cloudinaryService = cloudinaryService;
        this.modelMapper = modelMapper;
        this.userEditProfileValidation = userEditProfileValidation;
        this.userRegisterValidation = userRegisterValidation;
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    @PageTitle("Register")
    public ModelAndView register(@ModelAttribute(name = "model") UserRegisterBindingModel model) {
        return view("/users/register");
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerConfirm(@ModelAttribute(name = "model") UserRegisterBindingModel model, BindingResult bindingResult) {
        this.userRegisterValidation.validate(model, bindingResult);
        if (bindingResult.hasErrors()) {
            return view("/users/register");
        }

        UserServiceModel userServiceModel = this.modelMapper.map(model, UserServiceModel.class);
        this.userService.registerUser(userServiceModel);

        return view("/users/login");
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    @PageTitle("Login")
    public ModelAndView login() {
        return view("/users/login");
    }

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Home Page")
    public ModelAndView home(ModelAndView modelAndView) {
        List<CategoryViewModel> categories =
                this.categoryService
                        .findAllCategories()
                        .stream()
                        .map(categoryServiceModel -> this.modelMapper.map(categoryServiceModel, CategoryViewModel.class))
                        .collect(Collectors.toList());

        modelAndView.addObject("categories", categories);

        return super.view("/users/home", modelAndView);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Profile")
    public ModelAndView profile(Principal principal, ModelAndView modelAndView) {
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        UserProfileViewModel userProfileViewModel = this.modelMapper.map(userServiceModel, UserProfileViewModel.class);
        modelAndView.addObject("model", userProfileViewModel);

        return view("/users/profile", modelAndView);
    }

    @GetMapping("/edit-profile")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Edit Profile")
    public ModelAndView editProfile(Principal principal, ModelAndView modelAndView) {
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        UserProfileViewModel userProfileViewModel = this.modelMapper.map(userServiceModel, UserProfileViewModel.class);

        modelAndView.addObject("model", userProfileViewModel);

        return view("/users/edit-profile", modelAndView);
    }

    @PatchMapping("/edit-profile")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfileConfirm(@ModelAttribute(name = "model") UserEditBindingModel model, BindingResult bindingResult) throws IOException {
        this.userEditProfileValidation.validate(model, bindingResult);
        if (bindingResult.hasErrors()) {
            return view("/users/edit-profile");
        }

        UserServiceModel userServiceModel = this.modelMapper.map(model, UserServiceModel.class);
        if (!model.getImage().isEmpty()) {
            userServiceModel.setImageUrl(
                    this.cloudinaryService.uploadImage(model.getImage())
            );
        }
        String oldPassword = model.getOldPassword();
        this.userService.editServiceProfile(userServiceModel, oldPassword);

        return redirect("/users/profile");
    }

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PageTitle("All Users")
    public ModelAndView allUsers(ModelAndView modelAndView) {
        List<UserViewModel> users = this.userService.findAllUsers()
                .stream()
                .map(userServiceModel -> {
                    UserViewModel user = this.modelMapper.map(userServiceModel, UserViewModel.class);
                    user.setAuthorities(userServiceModel.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet()));

                    return user;
                })
                .collect(Collectors.toList());

        modelAndView.addObject("users", users);

        return view("/users/all-users", modelAndView);
    }

    @PostMapping("/set-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setUser(@PathVariable String id) {
        this.userService.setRole(id, USER_STRING);

        return redirect("/users/all-users");
    }

    @PostMapping("/set-moderator/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setModerator(@PathVariable String id) {
        this.userService.setRole(id, MODERATOR_STRING);

        return redirect("/users/all-users");
    }

    @PostMapping("/set-admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setAdmin(@PathVariable String id) {
        this.userService.setRole(id, ADMIN_STRING);

        return redirect("/users/all-users");
    }

    //If an user leave a field empty, this method sets its' value to 'null' instead of '' (empty String)
    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

}
