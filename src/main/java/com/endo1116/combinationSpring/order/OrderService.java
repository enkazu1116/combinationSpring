package com.endo1116.combinationSpring.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        log.info("注文を作成しました: {}", savedOrder.getId());
        
        // ドメインイベントを発行
        OrderCreatedEvent event = OrderCreatedEvent.from(savedOrder);
        eventPublisher.publishEvent(event);
        log.info("OrderCreatedEventを発行しました: {}", event);
        
        return savedOrder;
    }
    
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(String customerName) {
        return orderRepository.findByCustomerName(customerName);
    }
    
    @Transactional
    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("注文ステータスを更新しました: {} -> {}", id, status);
        return updatedOrder;
    }
    
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
        log.info("注文を削除しました: {}", id);
    }
}

