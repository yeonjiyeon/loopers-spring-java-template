//package com.loopers.domain.order;
//
//import com.loopers.domain.common.vo.Price;
//
//public record OrderItem(
//        Long productId,
//        String productName,
//        Integer quantity,
//        Price price
//) {
//    public Integer getTotalPrice() {
//        return this.price.amount() * this.quantity;
//    }
//}
