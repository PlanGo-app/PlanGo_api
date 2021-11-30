package com.plango.api.common.constant;

public final class ExceptionMessage {
    private ExceptionMessage() {}

    public static final String CURRENT_USER_NOT_FOUND = "Current user not found";
    public static final String CURRENT_USER_CANNOT_BE_AUTHENTICATED = "Could not authenticate current user";
    public static final String CURRENT_USER_NOT_ALLOWED_TO_GET_PLANNING_EVENT = "Current user not allowed to get requested planning event";
    public static final String CURRENT_USER_NOT_ALLOWED_TO_CREATE_PLANNING_EVENT = "Current user not allowed to create planning event on this travel";
    public static final String CURRENT_USER_NOT_ALLOWED_TO_UPDATE_PLANNING_EVENT = "Current user not allowed to update requested planning event";
    public static final String CURRENT_USER_NOT_ALLOWED_TO_GET_PIN = "Current user not allowed to get requested pin";
    public static final String CURRENT_USER_NOT_ALLOWED_TO_CREATE_PIN = "Current user not allowed to create requested pin in this travel";
    public static final String CURRENT_USER_NOT_ALLOWED_TO_UPDATE_PIN = "Current user not allowed to update requested pin";
    public static final String TRAVEL_NOT_FOUND = "Travel not found";
    public static final String PLANNING_EVENT_NOT_FOUND = "Planning event not found";
    public static final String PLANNING_EVENT_MUST_BE_LINKED_TO_PIN = "Planning event should be linked to a pin";
    public static final String PIN_NOT_FOUND = "Pin not found";
    public static final String PIN_ALREADY_EXIST = "Couldn't create the pin. A pin with the same location already exists";
    public static final String DATE_START_SHOULD_BE_BEFORE_DATE_END = "Date start of the planning event should be before its date end";
    public static final String USER_ALREADY_EXISTS = "User account already exists";
    public static final String PSEUDO_EMAIL_TAKEN = "Pseudo or email already taken";
}
