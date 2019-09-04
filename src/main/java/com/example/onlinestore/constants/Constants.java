package com.example.onlinestore.constants;

public final class Constants {

    public static final String EMPTY_STRING = "";

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_ROOT = "ROLE_ROOT";

    public static final String USER_STRING = "user";
    public static final String MODERATOR_STRING = "moderator";
    public static final String ADMIN_STRING = "admin";
    public static final String SUCCESS_STRING = "success";

    public final static String WISHLIST_CONST = "wishlist";
    public final static String SHOPPING_CART_CONST = "shopping-cart";
    public final static String ORDERS_CONST = "orders";
    public final static String ID_CONST = "id";
    public final static String NAME_CONST = "name";

    public static final String USERNAME_NOT_FOUND_EXCEPTION_MESSAGE = "Username not found!";
    public static final String CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE = "Category with this id does not exist!";
    public static final String PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE = "Product with this id does not exist!";
    public static final String ORDER_NOT_FOUND_EXCEPTION_MESSAGE = "Order with this id does not exist!";
    public static final String WISHLIST_NOT_FOUND_EXCEPTION_MESSAGE = "Wishlist with this id does not exist!";
    public static final String DUPLICATE_USER_EXCEPTION_MESSAGE = "User with this username already exists!";
    public static final String DUPLICATE_EMAIL_EXCEPTION_MESSAGE = "User with this email already exists!";
    public final static String EMPTY_LIST_NAME = "emptyList";
    public final static String EMPTY_LIST_SINGULAR_MESSAGE = "Your %s is empty!";
    public final static String EMPTY_LIST_PLURAL_MESSAGE = "Your %s are empty!";
    public final static String EMPTY_MODERATOR_ORDERS_MESSAGE = "There are currently no orders!";

    public static final String NAME_LENGTH_VALIDATION = "Name length validation";
    public static final String CATEGORY_ALREADY_EXISTS_VALIDATION_MESSAGE = "Category with name '%s' already exists!";
    public static final String NAME_MUST_CONTAINS_SYMBOLS_MESSAGE = "Name must contains at least 3 symbols!";
    public static final String INCORRECT_OLD_PASSWORD_VALIDATION_CODE = "Incorrect password!";
    public static final String INCORRECT_OLD_PASSWORD_VALIDATION_MESSAGE = "Incorrect password!";
    public static final String INCORRECT_PASSWORD_VALIDATION_CODE = "Passwords not matching";
    public static final String INCORRECT_PASSWORD_VALIDATION_MESSAGE = "Passwords don't match!";

    public final static String MODEL_ATTRIBUTE = "model";
    public final static String CATEGORY_ATTRIBUTE = "category";
    public final static String CATEGORIES_ATTRIBUTE = "categories";
    public final static String USERS_ATTRIBUTE = "users";
    public final static String ORDER_ATTRIBUTE = "order";
    public final static String ORDERS_ATTRIBUTE = "orders";
    public final static String PRODUCT_ATTRIBUTE = "product";
    public final static String PRODUCTS_ATTRIBUTE = "products";

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

    //ProductController Constants
    public final static String ADDED_ATTRIBUTE = "added";
    public final static String ADDED_MESSAGE = "%s is/are in your wishlist!";
    public final static String ALL_CONST = "all";

    //URL Constants


}
