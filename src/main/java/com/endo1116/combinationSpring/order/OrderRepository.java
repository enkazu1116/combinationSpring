package com.endo1116.combinationSpring.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerName(String customerName);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByProductId(Long productId);
}

