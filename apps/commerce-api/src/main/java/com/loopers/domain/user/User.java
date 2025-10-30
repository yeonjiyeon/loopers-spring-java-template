package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

  private static final String ID_PATTERN = "^[a-zA-Z0-9]{1,10}$";
  private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
  private static final String BIRTHDATE_PATTERN = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";

  @Id()
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  String userId;
  String email;
  String birthdate;
  private Gender gender;
  private int point;

  public enum Gender {
    MALE, FEMALE
  }

  protected User() {
  }

  public User(String userId, String email, String birthdate, Gender gender) {

    if (userId == null || !userId.matches(ID_PATTERN)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자를 포함한 1~10자 형식에 맞아야 합니다.");
    }

    if (email == null || !email.matches(EMAIL_PATTERN)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "이메일이 'xx@yy.zz' 형식에 맞지 않습니다.");
    }

    if (birthdate == null || !birthdate.matches(BIRTHDATE_PATTERN)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "생년월일이 'yyyy-MM-dd' 형식에 맞지 않습니다.");
    }

    this.userId = userId;
    this.email = email;
    this.birthdate = birthdate;
    this.gender = gender;
    this.point = 0;
  }

  public User(String userId, String email, String birthdate) {

    if (userId == null || !userId.matches(ID_PATTERN)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자를 포함한 1~10자 형식에 맞아야 합니다.");
    }

    if (email == null || !email.matches(EMAIL_PATTERN)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "이메일이 'xx@yy.zz' 형식에 맞지 않습니다.");
    }

    if (birthdate == null || !birthdate.matches(BIRTHDATE_PATTERN)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "생년월일이 'yyyy-MM-dd' 형식에 맞지 않습니다.");
    }

    this.userId = userId;
    this.email = email;
    this.birthdate = birthdate;
  }

  public String getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getBirthdate() {
    return birthdate;
  }
}
