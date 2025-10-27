package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.util.regex.Pattern;

public class UserBirth {

    private static final Pattern PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");


    private final String birth;

    public UserBirth(String birth) {
        if (birth == null || birth.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일이 비어 있을 수 없습니다.");
        }

        if(!PATTERN.matcher(birth).matches()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 맞지 않습니다.");
        }

        this.birth = birth;
    }
}
