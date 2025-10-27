package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.util.regex.Pattern;

public class UserId {

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,10}$");

    private final String id;

    public UserId(String id) {
        if(id == null || id.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 비어있을 수 없습니다.");
        }

        if (!PATTERN.matcher(id).matches()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 영문 및 숫자 10자 이내여야 합니다.");
        }
        this.id = id;
    }
}
