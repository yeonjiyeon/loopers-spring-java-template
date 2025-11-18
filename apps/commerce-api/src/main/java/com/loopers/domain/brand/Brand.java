package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brand")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  public Brand(String name, String description) {
    if (name == null || name.isBlank()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "이름은 필수입니다.");
    }

    if (description == null || description.isBlank()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "설명은 필수입니다.");
    }

    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
