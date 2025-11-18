package com.loopers.application.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.order.OrderV1Dto.OrderItemRequest;
import com.loopers.interfaces.order.OrderV1Dto.OrderRequest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class OrderFacade {
  private final ProductService productService;
  private final UserService userService;
  private final OrderService orderService;
  private final PointService pointService;

  @Transactional
  public OrderInfo placeOrder(String userId, OrderRequest request) {

    if (userId == null) {
      throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 필수입니다.");
    }

    User user = userService.getUser(userId);

    List<Long> productIds = request.items().stream()
        .map(OrderItemRequest::productId)
        .toList();

    List<Product> products = productService.getProducts(productIds);
    long totalAmount = orderService.calculateTotal(products, request.items());


    productService.deductStock(products, request.items());

    pointService.deductPoint(user.getId(), totalAmount);

    List<OrderItem> orderItems = buildOrderItems(products, request.items());
    Order order = orderService.createOrder(user.getId(), orderItems);

    return OrderInfo.from(order);
  }

  private List<OrderItem> buildOrderItems(List<Product> products, List<OrderItemRequest> items) {

    Map<Long, Product> productMap = products.stream()
        .collect(Collectors.toMap(Product::getId, p -> p));

    return items.stream()
        .map(i -> new OrderItem(productMap.get(i.productId()), i.quantity()))
        .toList();
  }
}
