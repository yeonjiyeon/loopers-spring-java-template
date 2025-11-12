package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "brand")
public class Brand extends BaseEntity {

  private String name;
  private String description;

  protected Brand() {
  }

  public Brand(String name, String description) {
    if (name == null || name.isBlank()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "이름은 필수입니다.");
    }

    if (description == null || description.isBlank()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "이름은 필수입니다.");
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
