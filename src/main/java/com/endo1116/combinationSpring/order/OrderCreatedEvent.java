package com.endo1116.combinationSpring.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.modulith.events.Externalized;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ドメインイベント：注文が作成されたときに発行される
 * 
 * @Externalized アノテーションにより、このイベントはKafkaに外部化されます
 * - 内部ではProductドメインが受信
 * - 外部ではKafkaを通じて他のマイクロサービスに通知
 * 
 * Outboxパターン：
 * 1. イベントがデータベースに永続化
 * 2. トランザクションコミット後にKafkaに発行
 * 3. 発行失敗時は自動リトライ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Externalized("order.created::#{#this.orderId}")  // Kafkaトピック: order.created, Key: orderId
public class OrderCreatedEvent implements Serializable {
    
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private String customerName;
    private LocalDateTime createdAt;
    
    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(
            order.getId(),
            order.getProductId(),
            order.getQuantity(),
            order.getCustomerName(),
            order.getCreatedAt()
        );
    }
}

