# Kafka & Redis 実装ガイド

## 概要

このプロジェクトでは、Spring Modulithを使用して以下を実装しています：

1. **Kafka Outboxパターン** - イベントの外部発行と信頼性の保証
2. **Redisキャッシュ** - 商品情報のキャッシング
3. **Redis分散ロック** - 在庫更新時の競合制御

## アーキテクチャ

```
┌─────────────────────────────────────────────────────────────┐
│                    Spring Modulith Application              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Order Domain                    Product Domain            │
│  ┌──────────────┐               ┌──────────────┐          │
│  │OrderService  │               │ProductService│          │
│  │              │               │              │          │
│  │ 1. 注文作成   │  内部イベント  │ 2. 在庫更新   │          │
│  │ 2. イベント発行├──────────────►│ (分散ロック) │          │
│  └──────┬───────┘               └──────────────┘          │
│         │                                                   │
│         │ @Externalized                                     │
│         ▼                                                   │
│  ┌─────────────────┐                                       │
│  │event_publication│  Outboxパターン                        │
│  │  (SQLite)       │                                       │
│  └────────┬────────┘                                       │
└───────────┼──────────────────────────────────────────────┘
            │
            │ Spring Modulith が自動的に
            │ Kafkaに発行
            ▼
     ┌─────────────┐
     │   Kafka     │
     │ Topic:      │
     │order.created│
     └──────┬──────┘
            │
            │ 外部システムが消費
            ▼
     ┌─────────────────┐
     │External Systems │
     │ - 配送システム    │
     │ - 分析システム    │
     │ - 通知サービス    │
     └─────────────────┘
```

## Kafka Outboxパターン

### 実装の仕組み

1. **イベント発行** (`OrderService`)
```java
@Transactional
public Order createOrder(Order order) {
    Order savedOrder = orderRepository.save(order);
    OrderCreatedEvent event = OrderCreatedEvent.from(savedOrder);
    eventPublisher.publishEvent(event);  // イベント発行
    return savedOrder;
}
```

2. **イベントの外部化設定** (`OrderCreatedEvent`)
```java
@Externalized("order.created::#{#this.orderId}")
public class OrderCreatedEvent implements Serializable {
    // トピック: order.created
    // パーティションキー: orderId
}
```

3. **Outboxテーブルへの永続化**
   - Spring Modulithが自動的に`event_publication`テーブルに保存
   - トランザクションコミット後に処理

4. **Kafkaへの非同期発行**
   - バックグラウンドでKafkaに発行
   - 発行成功後、DBから削除
   - 失敗時は自動リトライ

### Outboxパターンのメリット

✅ **トランザクション整合性**
- イベント発行とDB更新が同じトランザクション内
- 「注文は作成されたがイベントは発行されない」が起きない

✅ **信頼性**
- イベントはDBに永続化されるため紛失しない
- Kafka障害時もイベントは保持される

✅ **自動リトライ**
- 発行失敗時は自動的にリトライ
- 手動でのリカバリ処理が不要

### Kafkaトピック

| トピック名 | 説明 | パーティションキー |
|-----------|------|------------------|
| `order.created` | 注文作成イベント | orderId |

## Redis活用

### 1. キャッシュ機能

商品情報を Redis にキャッシュして、DBアクセスを削減します。

```java
@Cacheable(value = "products", key = "#id")
public Product getProductById(Long id) {
    // キャッシュにあれば返す
    // なければDBから取得してキャッシュに保存
    return productRepository.findById(id)...;
}

@CachePut(value = "products", key = "#id")
public Product updateProduct(Long id, Product productDetails) {
    // 更新時はキャッシュも更新
}

@CacheEvict(value = "products", key = "#id")
public void deleteProduct(Long id) {
    // 削除時はキャッシュからも削除
}
```

**キャッシュ設定:**
- TTL: 30分（商品情報）
- シリアライゼーション: JSON

### 2. 分散ロック

在庫更新時にRedisの分散ロックを使用して競合を防止します。

```java
@ApplicationModuleListener
@CacheEvict(value = "products", key = "#event.productId")
public void handleOrderCreated(OrderCreatedEvent event) {
    String lockKey = "product:stock:lock:" + event.getProductId();
    RLock lock = redissonClient.getLock(lockKey);
    
    try {
        // ロック取得（最大10秒待機、保持時間30秒）
        boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
        
        if (isLocked) {
            // 在庫を更新
            Product product = productRepository.findById(...)...;
            product.decreaseStock(event.getQuantity());
            productRepository.save(product);
        }
    } finally {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
```

**分散ロックのメリット:**
- 複数インスタンスでも安全
- 同時に同じ商品の在庫更新を防止
- デッドロックを自動的に回避

## セットアップ

### 1. Docker環境の起動

```bash
# Kafka、Redis、管理UIを起動
docker-compose up -d

# 起動確認
docker-compose ps
```

起動するサービス：
- Kafka (localhost:9092)
- Redis (localhost:6379)
- Kafka UI (http://localhost:8090)
- Redis Commander (http://localhost:8091)

### 2. アプリケーションの起動

```bash
# ビルド
./gradlew clean build -x test

# 起動
./gradlew bootRun
```

### 3. 動作確認

#### Kafka Outboxパターンの確認

```bash
# 1. 商品を作成
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"テストPC","description":"高性能","price":100000,"stockQuantity":10,"category":"電化製品"}'

# 2. 注文を作成（イベントが発行される）
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"customerName":"山田太郎","quantity":2,"totalPrice":200000,"status":"PENDING"}'

# 3. アプリケーションログを確認
# - "OrderCreatedEventを発行しました"
# - "外部システムがKafkaイベントを受信しました"
# のログが表示されるはず
```

#### Kafka UIで確認

1. http://localhost:8090 にアクセス
2. Topics → `order.created` を選択
3. Messages タブで発行されたイベントを確認

#### Redisキャッシュの確認

```bash
# 1回目: DBから取得（ログに "DBから商品を取得します" と表示）
curl http://localhost:8080/api/products/1

# 2回目: キャッシュから取得（ログに表示されない）
curl http://localhost:8080/api/products/1
```

#### Redis Commanderで確認

1. http://localhost:8091 にアクセス
2. `products::1` キーを確認
3. キャッシュされた商品情報を確認

#### 分散ロックの確認

```bash
# 複数の注文を同時に作成
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d '{"productId":1,"customerName":"顧客'$i'","quantity":1,"totalPrice":100000,"status":"PENDING"}' &
done

# ログで分散ロックの取得・解放を確認
# - "分散ロックを取得しました: product:stock:lock:1"
# - "分散ロックを解放しました: product:stock:lock:1"
```

## トラブルシューティング

### Kafkaに接続できない

```bash
# Kafkaの状態確認
docker-compose ps kafka
docker-compose logs kafka

# 再起動
docker-compose restart kafka
```

### Redisに接続できない

```bash
# Redisの状態確認
docker-compose ps redis
docker-compose logs redis

# 手動接続テスト
docker exec -it combinationspring-redis redis-cli ping
```

### イベントがKafkaに発行されない

1. `event_publication` テーブルを確認
   ```sql
   SELECT * FROM event_publication WHERE completion_date IS NULL;
   ```

2. Spring Modulithの設定を確認
   ```properties
   spring.modulith.events.externalization.enabled=true
   spring.modulith.events.kafka.enabled=true
   ```

3. アプリケーションログを確認

## パフォーマンス最適化

### Redisキャッシュ

- **キャッシュヒット率**: 80%以上を目標
- **TTL**: アクセス頻度に応じて調整
- **メモリ**: Redisのメモリ使用量をモニタリング

### Kafka

- **バッチサイズ**: 大量のイベント発行時は調整
- **パーティション**: スケーラビリティが必要な場合は増やす
- **レプリケーション**: 本番環境では3以上

## まとめ

### Kafka Outboxパターン
✅ トランザクション整合性の保証  
✅ イベント紛失の防止  
✅ 自動リトライ機能  
✅ 外部システムとの疎結合な連携

### Redis活用
✅ キャッシュによるパフォーマンス向上  
✅ 分散ロックによる競合制御  
✅ スケーラブルなアーキテクチャ

このアーキテクチャにより、信頼性が高くスケーラブルなマイクロサービス環境を実現できます。

