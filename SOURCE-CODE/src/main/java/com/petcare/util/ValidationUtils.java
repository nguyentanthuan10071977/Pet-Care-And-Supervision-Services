package com.petcare.util;

import com.petcare.exception.ApiException;

import java.util.regex.Pattern;

/**
 * Kiểm tra các quy tắc nghiệp vụ lấy trực tiếp từ Acceptance Criteria trong
 * User Story "Login" / "Register":
 *  - Số điện thoại: đúng 10 hoặc 11 số, không chứa chữ/ký tự khác.
 *  - Mật khẩu: ký tự đầu không phải là số, dài hơn 6 ký tự, không chứa ký tự đặc biệt.
 */
public final class ValidationUtils {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,11}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9]{6,}$");

    private ValidationUtils() {
    }

    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw ApiException.badRequest(
                    "Số điện thoại phải gồm 10 hoặc 11 chữ số, không chứa ký tự khác.");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw ApiException.badRequest(
                    "Mật khẩu phải dài hơn 6 ký tự, ký tự đầu không phải là số, " +
                    "và không chứa ký tự đặc biệt.");
        }
    }
}
