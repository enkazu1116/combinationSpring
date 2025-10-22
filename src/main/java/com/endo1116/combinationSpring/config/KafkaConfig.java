package com.endo1116.combinationSpring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

/**
 * Kafka設定とOutboxパターン
 * 
 * Spring Modulith の Outbox パターン：
 * 1. イベントはまずデータベースに永続化される（event_publication テーブル）
 * 2. その後、バックグラウンドでKafkaに発行される
 * 3. 発行が成功したらデータベースから削除される
 * 
 * これにより、イベント発行の信頼性が保証されます。
 * 
 * 設定は application.properties で行います：
 * - spring.modulith.events.externalization.enabled=true
 * - spring.modulith.events.kafka.enabled=true
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    /**
     * Kafkaメッセージのシリアライゼーション設定
     */
    @Bean
    public RecordMessageConverter messageConverter() {
        return new JsonMessageConverter();
    }
    
    /**
     * Spring Modulith が自動的に以下の処理を行います：
     * 
     * 1. @Externalized アノテーションが付いたイベントを検出
     * 2. イベントをデータベースに永続化
     * 3. Kafkaへの発行をスケジュール
     * 4. 発行成功後、データベースから削除
     * 
     * Kafka トピック名は自動的に決定されます：
     * - デフォルト: イベントクラス名（例: OrderCreatedEvent）
     * - カスタマイズ: @Externalized(target = "custom-topic") で指定可能
     */
}

