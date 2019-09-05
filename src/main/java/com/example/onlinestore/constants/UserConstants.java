package com.example.onlinestore.constants;

public final class UserConstants {

    //UserController Constants
    public final static String CUSTOMER_BALANCE = "customer-balance";
    public final static String PASSWORDS_DONT_MATCH_ATTRIBUTE = "passDontMatch";
    public final static String PASSWORDS_DONT_MATCH_MESSAGE = "Passwords don't match!";
    public final static String G_RECAPTCHA_RESPONSE = "g-recaptcha-response";
    public final static String RECAPTCHA_ERROR_ATTRIBUTE = "recaptchaError";
    public final static String RECAPTCHA_ERROR_MESSAGE = "You did not complete reCaptcha. Please try again!";
    public final static String SUCCESSFUL_REGISTER_ATTRIBUTE = "successfulRegistration";
    public final static String SUCCESSFUL_REGISTER_MESSAGE = "You have registered successfully, %s!";
    public final static String YOU_CAN_LOGIN_ATTRIBUTE = "youCanLogin";
    public final static String YOU_CAN_LOGIN_MESSAGE = "You can login now.";
    public final static String PROFILE_PICTURE_ATTRIBUTE = "profilePicture";

    //Users URL Constants etc
    public static final String USERS_REQUEST_MAPPING = "/users";

    public static final String REGISTER_GET = "/register";
    public static final String REGISTER_POST = "/register";
    public static final String LOGIN_GET = "/login";
    public static final String HOME_GET = "/home";
    public static final String PROFILE_GET = "/profile";
    public static final String ADD_MONEY_POST = "/add-money";
    public static final String EDIT_PROFILE_GET = "/edit-profile";
    public static final String EDIT_PROFILE_PATCH = "/edit-profile";
    public static final String ALL_USERS_GET = "/all-users";
    public static final String SET_USER_BY_ID_POST = "/set-user/{id}";
    public static final String SET_MODERATOR_BY_ID_POST = "/set-moderator/{id}";
    public static final String SET_ADMIN_BY_ID_POST = "/set-admin/{id}";

    public static final String USERS_REGISTER_VIEW_NAME = "/users/register";
    public static final String USERS_LOGIN_VIEW_NAME = "/users/login";
    public static final String USERS_HOME_VIEW_NAME = "/users/home";
    public static final String USERS_PROFILE_VIEW_NAME = "/users/profile";
    public static final String USERS_PROFILE_URL = "/users/profile";
    public static final String USERS_EDIT_PROFILE_VIEW_NAME = "/users/edit-profile";
    public static final String USERS_ALL_USERS_VIEW_NAME = "/users/all-users";
    public static final String USERS_ALL_USERS_URL = "/users/all-users";

}
