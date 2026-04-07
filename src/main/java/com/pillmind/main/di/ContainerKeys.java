package com.pillmind.main.di;

public final class ContainerKeys {

    private ContainerKeys() {
    }

    // Infrastructure
    public static final String DATABASE_JDBI = "database.jdbi";
    public static final String CRYPTO_HASHER = "crypto.hasher";
    public static final String CRYPTO_JWT = "crypto.jwt";
    public static final String SHARED_OBJECT_MAPPER = "shared.object-mapper";

    // Repositories
    public static final String REPOSITORY_USER = "repository.user";
    public static final String REPOSITORY_LOCAL_ACCOUNT = "repository.local-account";
    public static final String REPOSITORY_OAUTH_ACCOUNT = "repository.oauth-account";
    public static final String REPOSITORY_USER_IMAGE = "repository.user-image";
    public static final String STORAGE_IMAGE_GATEWAY = "storage.image-gateway";

    // OAuth
    public static final String OAUTH_GOOGLE_VALIDATOR = "oauth.google-validator";

    // Use cases
    public static final String USECASE_CREATE_LOCAL_ACCOUNT = "usecase.create-local-account";
    public static final String USECASE_LOCAL_AUTHENTICATION = "usecase.local-authentication";
    public static final String USECASE_LOAD_USER_BY_ID = "usecase.load-user-by-id";
    public static final String USECASE_UPDATE_USER_PROFILE = "usecase.update-user-profile";
    public static final String USECASE_LINK_OAUTH_ACCOUNT = "usecase.link-oauth-account";
    public static final String USECASE_REQUEST_IMAGE_UPLOAD = "usecase.request-image-upload";
    public static final String USECASE_CONFIRM_IMAGE_UPLOAD = "usecase.confirm-image-upload";

    // Repositories — Medicine
    public static final String REPOSITORY_MEDICINE = "repository.medicine";
    public static final String REPOSITORY_MEDICINE_DOSE = "repository.medicine-dose";
    public static final String REPOSITORY_REMINDER = "repository.reminder";

    // Use cases — Medicine
    public static final String USECASE_CREATE_MEDICINE = "usecase.create-medicine";
    public static final String USECASE_UPDATE_MEDICINE = "usecase.update-medicine";
    public static final String USECASE_DELETE_MEDICINE = "usecase.delete-medicine";
    public static final String USECASE_LOAD_MEDICINES_BY_USER = "usecase.load-medicines-by-user";
    public static final String USECASE_LOAD_MEDICINE_BY_ID = "usecase.load-medicine-by-id";
    public static final String USECASE_TAKE_MEDICINE_DOSE = "usecase.take-medicine-dose";
    public static final String USECASE_SKIP_MEDICINE_DOSE = "usecase.skip-medicine-dose";
    public static final String USECASE_LOAD_DOSES_BY_DATE = "usecase.load-doses-by-date";
    public static final String USECASE_LOAD_DOSES_BY_MEDICINE_AND_DATE = "usecase.load-doses-by-medicine-and-date";
    public static final String USECASE_CREATE_REMINDER = "usecase.create-reminder";
    public static final String USECASE_LOAD_REMINDERS_BY_USER = "usecase.load-reminders-by-user";
    public static final String USECASE_LOAD_REMINDERS_BY_MEDICINE = "usecase.load-reminders-by-medicine";
    public static final String USECASE_UPDATE_REMINDER = "usecase.update-reminder";
    public static final String USECASE_DELETE_REMINDER = "usecase.delete-reminder";

    // Validators
    public static final String VALIDATOR_SIGNUP = "validator.signup";
    public static final String VALIDATOR_SIGNIN = "validator.signin";

    // Routes
    public static final String ROUTE_HEALTH = "route.health";
    public static final String ROUTE_SWAGGER = "route.swagger";
    public static final String ROUTE_AUTH = "route.auth";
    public static final String ROUTE_MEDICINE = "route.medicine";
    public static final String ROUTE_REMINDER = "route.reminder";
}
