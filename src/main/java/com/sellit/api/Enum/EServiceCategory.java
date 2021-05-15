package com.sellit.api.Enum;

import java.util.HashSet;
import java.util.Set;

public enum EServiceCategory {
    PART_AND_EVENT_SERVICES("Party and Event Services"),
    HOME_SERVICES("Home Services"),
    HEALTH_AND_WELLNESS_SERVICES("Health and Wellness Services"),
    TECHNOLOGY("Technology");

    // declaring private variable for getting values
    private String action;

    // getter method
    public String getName()
    {
        return this.action;
    }
    EServiceCategory(String action)
    {
        this.action = action;
    }

    private static final Set<String> _values = new HashSet<>();
    static{
        for (ERole choice : ERole.values()) {
            _values.add(choice.name().toUpperCase());
        }
    }
    public static boolean contains(String value){
        return _values.contains(value);
    }
}
