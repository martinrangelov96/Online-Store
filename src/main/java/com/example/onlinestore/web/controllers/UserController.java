package com.example.onlinestore.web.controllers;

import com.example.onlinestore.domain.models.binding.UserEditBindingModel;
import com.example.onlinestore.domain.models.binding.UserRegisterBindingModel;
import com.example.onlinestore.domain.models.service.RoleServiceModel;
import com.example.onlinestore.domain.models.service.UserServiceModel;
import com.example.onlinestore.domain.models.view.categories.CategoryViewModel;
import com.example.onlinestore.domain.models.view.users.UserEditProfileViewModel;
import com.example.onlinestore.domain.models.view.users.UserProfileViewModel;
import com.example.onlinestore.domain.models.view.users.UserViewModel;
import com.example.onlinestore.services.category.CategoryService;
import com.example.onlinestore.services.cloudinary.CloudinaryService;
import com.example.onlinestore.services.recaptcha.RecaptchaService;
import com.example.onlinestore.services.user.UserService;
import com.example.onlinestore.validation.UserEditProfileValidator;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.onlinestore.constants.Constants.*;
import static com.example.onlinestore.constants.UserConstants.*;

@Controller
@RequestMapping(REQUEST_MAPPING_USERS)
public class UserController extends BaseController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;
    private final RecaptchaService recaptchaService;
    private final ModelMapper modelMapper;
    private final UserEditProfileValidator userEditProfileValidator;

    @Autowired
    public UserController(UserService userService, CategoryService categoryService, CloudinaryService cloudinaryService, RecaptchaService recaptchaService, ModelMapper modelMapper, UserEditProfileValidator userEditProfileValidator) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.cloudinaryService = cloudinaryService;
        this.recaptchaService = recaptchaService;
        this.modelMapper = modelMapper;
        this.userEditProfileValidator = userEditProfileValidator;
    }

    @GetMapping(REGISTER_GET)
    @PreAuthorize(IS_ANONYMOUS)
    @PageTitle(REGISTER_PAGE_TITLE)
    public ModelAndView register(@ModelAttribute(name = MODEL_ATTRIBUTE) UserRegisterBindingModel model) {
        return view(USERS_REGISTER_VIEW_NAME);
    }

    @PostMapping(REGISTER_POST)
    @PreAuthorize(IS_ANONYMOUS)
    public ModelAndView registerConfirm(@ModelAttribute(name = MODEL_ATTRIBUTE) UserRegisterBindingModel model,
                                        @RequestParam(name = G_RECAPTCHA_RESPONSE) String gRecaptchaResponse,
                                        ModelAndView modelAndView,
                                        HttpServletRequest request) {

        this.userService.existsByUsername(model.getUsername());
        this.userService.existsByEmail(model.getEmail());
        if (!model.getPassword().equals(model.getConfirmPassword())) {
            modelAndView.addObject(PASSWORDS_DONT_MATCH_ATTRIBUTE, PASSWORDS_DONT_MATCH_MESSAGE);
            return view(USERS_REGISTER_VIEW_NAME, modelAndView);
        }

        if (this.recaptchaService.verifyRecaptcha(request.getRemoteAddr(), gRecaptchaResponse) == null) {
            modelAndView.addObject(RECAPTCHA_ERROR_ATTRIBUTE, RECAPTCHA_ERROR_MESSAGE);
            return view(USERS_REGISTER_VIEW_NAME, modelAndView);
        }

        UserServiceModel userServiceModel = this.modelMapper.map(model, UserServiceModel.class);

        this.userService.registerUser(userServiceModel);
        modelAndView.addObject(SUCCESSFUL_REGISTER_ATTRIBUTE, String.format(SUCCESSFUL_REGISTER_MESSAGE, model.getUsername()));
        modelAndView.addObject(YOU_CAN_LOGIN_ATTRIBUTE, YOU_CAN_LOGIN_MESSAGE);
        return view(USERS_LOGIN_VIEW_NAME, modelAndView);
    }

    @GetMapping(LOGIN_GET)
    @PreAuthorize(IS_ANONYMOUS)
    @PageTitle(LOGIN_PAGE_TITLE)
    public ModelAndView login() {
        return view(USERS_LOGIN_VIEW_NAME);
    }

    @GetMapping(HOME_GET)
    @PreAuthorize(IS_AUTHENTICATED)
    @PageTitle(HOME_PAGE_PAGE_TITLE)
    public ModelAndView home(ModelAndView modelAndView) {
        List<CategoryViewModel> categories =
                this.categoryService
                        .findAllCategories()
                        .stream()
                        .map(categoryServiceModel -> this.modelMapper.map(categoryServiceModel, CategoryViewModel.class))
                        .collect(Collectors.toList());

        modelAndView.addObject(CATEGORIES_ATTRIBUTE, categories);
        return super.view(USERS_HOME_VIEW_NAME, modelAndView);
    }

    @GetMapping(PROFILE_GET)
    @PreAuthorize(IS_AUTHENTICATED)
    @PageTitle(PROFILE_PAGE_TITLE)
    public ModelAndView profile(Principal principal, ModelAndView modelAndView) {
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        UserProfileViewModel userProfileViewModel = this.modelMapper.map(userServiceModel, UserProfileViewModel.class);

        modelAndView.addObject(MODEL_ATTRIBUTE, userProfileViewModel);
        return view(USERS_PROFILE_VIEW_NAME, modelAndView);
    }

    @PostMapping(ADD_MONEY_POST)
    @PreAuthorize(IS_AUTHENTICATED)
    public ModelAndView addMoneyToBalance(Principal principal, BigDecimal moneyToAdd) {
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());

        this.userService.addMoneyToBalance(userServiceModel, moneyToAdd);
        return redirect(USERS_PROFILE_URL);
    }

    @GetMapping(EDIT_PROFILE_GET)
    @PreAuthorize(IS_AUTHENTICATED)
    @PageTitle(EDIT_PROFILE_PAGE_TITLE)
    public ModelAndView editProfile(Principal principal, ModelAndView modelAndView) {
        UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
        UserEditProfileViewModel userEditProfileViewModel = this.modelMapper.map(userServiceModel, UserEditProfileViewModel.class);

        modelAndView.addObject(MODEL_ATTRIBUTE, userEditProfileViewModel);
        return view(USERS_EDIT_PROFILE_VIEW_NAME, modelAndView);
    }

    @PatchMapping(EDIT_PROFILE_PATCH)
    @PreAuthorize(IS_AUTHENTICATED)
    public ModelAndView editProfileConfirm(@ModelAttribute(name = MODEL_ATTRIBUTE) UserEditBindingModel model, BindingResult bindingResult) throws IOException {
        this.userEditProfileValidator.validate(model, bindingResult);
        if (bindingResult.hasErrors()) {
            return view(USERS_EDIT_PROFILE_VIEW_NAME);
        }

        UserServiceModel userServiceModel = this.modelMapper.map(model, UserServiceModel.class);
        if (!model.getImage().isEmpty()) {
            userServiceModel.setImageUrl(
                    this.cloudinaryService.uploadImage(model.getImage())
            );
        }
        String oldPassword = model.getOldPassword();

        this.userService.editProfile(userServiceModel, oldPassword);
        return redirect(USERS_PROFILE_URL);
    }

    @GetMapping(ALL_USERS_GET)
    @PreAuthorize(HAS_ROLE_ADMIN)
    @PageTitle(ALL_USERS_PAGE_TITLE)
    public ModelAndView allUsers(ModelAndView modelAndView) {
        List<UserViewModel> users = this.userService.findAllUsersOrderedByDate()
                .stream()
                .map(userServiceModel -> {
                    UserViewModel user = this.modelMapper.map(userServiceModel, UserViewModel.class);
                    user.setAuthorities(userServiceModel.getAuthorities()
                            .stream()
                            .map(RoleServiceModel::getAuthority)
                            .collect(Collectors.toSet()));

                    return user;
                })
                .collect(Collectors.toList());

        modelAndView.addObject(USERS_ATTRIBUTE, users);
        return view(USERS_ALL_USERS_VIEW_NAME, modelAndView);
    }

    @PostMapping(SET_USER_BY_ID_POST)
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ModelAndView setUser(@PathVariable String id) {
        this.userService.setRole(id, USER_STRING);
        return redirect(USERS_ALL_USERS_URL);
    }

    @PostMapping(SET_MODERATOR_BY_ID_POST)
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ModelAndView setModerator(@PathVariable String id) {
        this.userService.setRole(id, MODERATOR_STRING);
        return redirect(USERS_ALL_USERS_URL);
    }

    @PostMapping(SET_ADMIN_BY_ID_POST)
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ModelAndView setAdmin(@PathVariable String id) {
        this.userService.setRole(id, ADMIN_STRING);
        return redirect(USERS_ALL_USERS_URL);
    }

    //If an user leave a field empty, this method sets its' value to 'null' instead of '' (empty String)
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    //make the field public if you need valid state of fields (@Autowired, Dependency Injection)
    @InitBinder
    public void initUserBalanceAndProfilePicture(HttpSession httpSession, Principal principal) {
        if (principal != null) {
            UserServiceModel userServiceModel = this.userService.findUserByUsername(principal.getName());
            UserProfileViewModel userProfileViewModel = this.modelMapper.map(userServiceModel, UserProfileViewModel.class);

            httpSession.setAttribute(CUSTOMER_BALANCE, userProfileViewModel.getBalance());
            httpSession.setAttribute(PROFILE_PICTURE_ATTRIBUTE, userProfileViewModel.getImageUrl());
        }
    }

}
