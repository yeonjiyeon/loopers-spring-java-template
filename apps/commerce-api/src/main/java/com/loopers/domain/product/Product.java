package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product extends BaseEntity {

  private Long brandId;
  private String name;
  private String description;
  private long price;
  private int stock;

  protected Product() {
  }

  public Product(Long brandId, String name, String description, long price, int stock) {
    if (brandId == null) {
      throw new CoreException(ErrorType.BAD_REQUEST, "상품의 브랜드를 등록해야 합니다.");
    }

    if (name == null || name.isBlank()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "상품의 이름을 등록해야 합니다.");
    }

    if (description == null || description.isBlank()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "상품의 설명을 등록해야 합니다.");
    }

    if (price < 0) {
      throw new CoreException(ErrorType.BAD_REQUEST, "상품의 가격은 음수가 될 수 없습니다.");
    }

    if (stock < 0) {
      throw new CoreException(ErrorType.BAD_REQUEST, "상품의 재고는 음수가 될 수 없습니다.");
    }

    this.brandId = brandId;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
  }

  public Long getBrandId() {
    return brandId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public long getPrice() {
    return price;
  }

  public int getStock() {
    return stock;
  }

  public void deductStock(int requestedQty) {
    this.stock -= requestedQty;
  }
}
