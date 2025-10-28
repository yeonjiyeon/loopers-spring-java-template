package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.regex.Pattern;

@Entity
@Table(name = "user")
public class User extends BaseEntity {

    private static final Pattern USERID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,10}$");
    private static final Pattern BIRTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    private String userId;
    private String email;
    private String birth;

    protected User() {}

    public User(String userId, String email, String birth) {
        this.userId = requireValidUserId(userId);
        this.email = requireValidEmail(email);
        this.birth = requireValidBirthDate(birth);
    }

    String requireValidUserId(String userId) {
        if(userId == null || userId.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 비어있을 수 없습니다.");
        }

        if (!USERID_PATTERN.matcher(userId).matches()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 영문 및 숫자 10자 이내여야 합니다.");
        }
        return userId;
    }

    String requireValidEmail(String email) {
        if(email == null || email.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일이 비어 있을 수 없습니다.");
        }

        if(!EMAIL_PATTERN.matcher(email).matches()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일 형식에 맞지 않습니다. ex)xx@yy.zz");
        }
        return email;
    }

    String requireValidBirthDate(String birth) {
        if (birth == null || birth.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일이 비어 있을 수 없습니다.");
        }

        if(!BIRTH_PATTERN.matcher(birth).matches()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 맞지 않습니다.");
        }
        return birth;
    }
}
