package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.util.regex.Pattern;

public class UserEmail {

    private final static Pattern PATTERN = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    private String email;

    public UserEmail(String email) {
        if(email == null || email.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일이 비어 있을 수 없습니다.");
        }

        if(!PATTERN.matcher(email).matches()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일 형식에 맞지 않습니다. ex)xx@yy.zz");
        }

        this.email = email;
    }


}
