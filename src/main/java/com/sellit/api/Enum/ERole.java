package com.sellit.api.Enum;

import java.util.HashSet;
import java.util.Set;

public enum ERole {
    ROLE_CUSTOMER,
    ROLE_ADMIN,
    ROLE_PROVIDER;

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
