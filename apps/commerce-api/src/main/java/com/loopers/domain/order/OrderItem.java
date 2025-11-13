package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_item")
public class OrderItem extends BaseEntity {

  private Long productId;
  private Integer quantity;
  private Long price;


  public OrderItem(Product product, int quantity) {
    if(product.getStock() < quantity){
      throw new IllegalArgumentException("재고가 부족합니다");
    }
    this.productId = product.getId();
    this.quantity = quantity;
    this.price = product.getPrice();
  }

  public long calculateAmount() {
    return price * quantity;
  }
}
