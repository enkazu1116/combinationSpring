package com.endo1116.combinationSpring.external;

import com.endo1116.combinationSpring.order.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 外部システム向けのKafkaイベントリスナー
 * 
 * このクラスは外部システムをシミュレートしています。
 * 実際の環境では、別のマイクロサービスやシステムがこのイベントを受信します。
 * 
 * Outboxパターンの流れ：
 * 1. OrderServiceがイベントを発行
 * 2. Spring ModulithがDBに永続化（event_publicationテーブル）
 * 3. バックグラウンドでKafkaトピック"order.created"に発行
 * 4. このリスナーがイベントを受信（外部システムのシミュレート）
 */
@Component
@Slf4j
public class ExternalOrderEventListener {

    /**
     * Kafkaトピック "order.created" からイベントを受信
     * 
     * 実際の使用例：
     * - 配送システムへの通知
     * - 在庫管理システムへの通知
     * - データ分析システムへのイベント送信
     * - 通知サービスへの顧客通知依頼
     */
    @KafkaListener(
        topics = "order.created",
        groupId = "external-order-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleExternalOrderCreated(OrderCreatedEvent event) {
        log.info("=== 外部システムがKafkaイベントを受信しました ===");
        log.info("注文ID: {}", event.getOrderId());
        log.info("商品ID: {}", event.getProductId());
        log.info("数量: {}", event.getQuantity());
        log.info("顧客名: {}", event.getCustomerName());
        log.info("作成日時: {}", event.getCreatedAt());
        
        // ここで外部システムの処理を実行
        // 例：配送手配、在庫連携、通知送信など
        simulateExternalProcessing(event);
    }
    
    /**
     * 外部システムの処理をシミュレート
     */
    private void simulateExternalProcessing(OrderCreatedEvent event) {
        try {
            // 配送システムへの通知をシミュレート
            log.info("配送システムに通知を送信しています...");
            Thread.sleep(100);
            log.info("配送システムへの通知が完了しました");
            
            // データウェアハウスへのイベント送信をシミュレート
            log.info("データウェアハウスにイベントを送信しています...");
            Thread.sleep(100);
            log.info("データウェアハウスへの送信が完了しました");
            
            log.info("=== 外部システムの処理が完了しました ===");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("外部処理中にエラーが発生しました", e);
        }
    }
}

