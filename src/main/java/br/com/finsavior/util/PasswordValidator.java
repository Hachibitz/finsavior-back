package br.com.finsavior.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final Pattern UPPER_CASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWER_CASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[\\W_]");
    private static final int MIN_LENGTH = 8;

    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }

        boolean hasUpperCase = UPPER_CASE_PATTERN.matcher(password).find();
        boolean hasLowerCase = LOWER_CASE_PATTERN.matcher(password).find();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).find();
        boolean hasSpecialChar = SPECIAL_CHAR_PATTERN.matcher(password).find();
        boolean hasMinLength = password.length() >= MIN_LENGTH;

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar && hasMinLength;
    }
}
