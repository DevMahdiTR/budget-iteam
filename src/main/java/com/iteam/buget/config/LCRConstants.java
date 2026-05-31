package com.iteam.buget.config;

public final class LCRConstants {


    public static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])" +           // at least one digit
                    "(?=.*[a-z])" +            // at least one lowercase letter
                    "(?=.*[A-Z])" +            // at least one uppercase letter
                    "(?=.*[@#$%^&+=!])" +      // at least one special character
                    "(?=\\S+$)" +              // no whitespace
                    ".{8,}$";                  // at least 8 characters
}
