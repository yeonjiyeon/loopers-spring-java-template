package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public class User {
    private final String id;
    private final String email;

    private User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public static User create(String id, String email) {
        // ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.
        if (!id.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{1,10}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자 10자 이내여야 합니다.");
        }
        // 이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 올바르지 않습니다.");
        }

        return new User(id, email);
    }
}
