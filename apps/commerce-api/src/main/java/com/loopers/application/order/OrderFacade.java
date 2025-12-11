package com.loopers.application.order;

import com.loopers.domain.coupon.CouponDiscountResult;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand.Item;
import com.loopers.domain.order.OrderCommand.PlaceOrder;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentProcessor;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
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
@Transactional
public class OrderFacade {

  private final ProductService productService;
  private final UserService userService;
  private final OrderService orderService;
  private final PointService pointService;
  private final CouponService couponService;
  private final List<PaymentProcessor> paymentProcessors;

  @Transactional
  public OrderInfo placeOrder(PlaceOrder command) {

    User user = userService.findByIdWithLock(command.userId())
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "유저를 찾을 수 없습니다."));

    List<Long> productIds = command.items().stream()
        .map(Item::productId)
        .toList();

    List<Product> products = productService.getProducts(productIds);

    List<OrderItem> orderItems = buildOrderItems(products, command.items());
    Order order = orderService.createOrder(user.getId(), orderItems);
    long totalAmount = order.getTotalAmount().getValue();

    long finalPaymentAmount = totalAmount;

    if (command.couponId() != null) {
      CouponDiscountResult discountResult = couponService.useCouponAndCalculateDiscount(
          command.couponId(),
          totalAmount
      );

      finalPaymentAmount -= discountResult.discountAmount();
    }

    productService.deductStock(products, orderItems);

    PaymentProcessor processor = paymentProcessors.stream()
        .filter(p -> p.supports(command.paymentType())) // command에 paymentType 필드 추가 필요
        .findFirst()
        .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "지원하지 않는 결제 방식입니다."));

    Map<String, Object> paymentDetails = command.getPaymentDetails();

    Payment paymentResult = processor.process(
        order.getId(),
        user,
        finalPaymentAmount,
        paymentDetails
    );

    return OrderInfo.from(order);
  }

  private List<OrderItem> buildOrderItems(List<Product> products, List<Item> items) {

    Map<Long, Product> productMap = products.stream()
        .collect(Collectors.toMap(Product::getId, p -> p));

    return items.stream()
        .map(i -> new OrderItem(productMap.get(i.productId()), i.quantity()))
        .toList();
  }
}
