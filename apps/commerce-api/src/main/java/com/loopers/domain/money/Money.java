package com.loopers.domain.money;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

  private Long value;

  public Money(Long value) {
    if (value == null) {
      throw new CoreException(ErrorType.BAD_REQUEST);
    }
    if (value < 0) {
      throw new CoreException(ErrorType.BAD_REQUEST);
    }
    this.value = value;
  }

  public Long getValue() {
    return value;
  }
}
