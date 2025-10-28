package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public class User {
    private final String id;

    private User(String id) {
        this.id = id;
    }

    public static User create(String id) {
        if (!id.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{1,10}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자 10자 이내여야 합니다.");
        }
        return new User(id);
    }
}
