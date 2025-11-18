package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.money.Money;
import com.loopers.domain.product.Product;
import com.loopers.support.error.CoreException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

  @Test
  @DisplayName("유효한 상품과 수량으로 주문을 생성하면 정상적으로 생성된다.")
  void createOrder_success() {
    // arrange
    Product product1 = new Product(1L, "셔츠", "설명", new Money(30000L), 15);
    Product product2 = new Product(2L, "팬츠", "설명", new Money(50000L), 16);

    OrderItem item1 = new OrderItem(product1, 2);
    OrderItem item2 = new OrderItem(product2, 1);

    // act
    Order order = new Order(1L, List.of(item1, item2));

    // assert
    assertThat(order.getOrderItems()).hasSize(2);
    assertThat(order.calculateTotalAmount()).isEqualTo(30000 * 2 + 50000 * 1);
  }

  @Test
  @DisplayName("상품 리스트가 비어 있는 경우 주문 생성에 실패한다.")
  void createOrder_fail_dueToEmptyItems() {
    // when & then
    assertThatThrownBy(() -> new Order(1L, List.of()))
        .isInstanceOf(CoreException.class)
        .hasMessageContaining("주문 아이템은 필수입니다.");
  }

  @Test
  @DisplayName("userId가 null이면 주문 생성에 실패한다.")
  void createOrder_fail_dueToNullUserId() {
    Product product = new Product(1L, "셔츠", "설명", new Money(30000L), 10);
    OrderItem item = new OrderItem(product, 1);

    assertThatThrownBy(() -> new Order(null, List.of(item)))
        .isInstanceOf(CoreException.class)
        .hasMessageContaining("사용자 ID는 필수입니다.");
  }

  @Test
  @DisplayName("상품 재고보다 많은 수량으로 주문 아이템 생성 시 예외가 발생한다.")
  void createOrderItem_fail_dueToStock() {
    // given
    Product product = new Product(1L, "셔츠", "설명", new Money(30000L), 2);

    // when & then
    assertThatThrownBy(() -> new OrderItem(product, 3))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("재고가 부족합니다");
  }

  @Test
  @DisplayName("주문 금액이 정확히 계산된다.")
  void calculateTotalAmount_success() {
    // given
    Product productA = new Product(1L, "셔츠", "설명", new Money(10000L), 10);
    Product productB = new Product(2L, "청바지", "설명", new Money(20000L), 10);

    OrderItem itemA = new OrderItem(productA, 3); // 3만
    OrderItem itemB = new OrderItem(productB, 2); // 4만

    Order order = new Order(1L, List.of(itemA, itemB));

    // then
    assertThat(order.calculateTotalAmount()).isEqualTo(70000);
  }
}
