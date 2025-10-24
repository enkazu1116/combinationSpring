# Spring Modulith 実装サマリー

## 実装完了内容

### ✅ 1. Spring Modulithへの構成変更
- Spring Boot 3.4.2 へアップグレード（4.0.0-M3から変更）
- Spring Modulith 1.3.1 を導入
- Java 21 へ変更
- TursoデータベースのサポートにSQLiteドライバを追加

### ✅ 2. Order ドメイン実装
**パッケージ**: `com.endo1116.combinationSpring.order`

#### 実装したクラス：
- `Order.java` - 注文エンティティ
- `OrderRepository.java` - JPA リポジトリ
- `OrderService.java` - ビジネスロジック
- `OrderController.java` - REST API
- `OrderCreatedEvent.java` - ドメインイベント
- `package-info.java` - Spring Modulith モジュール定義

#### 機能：
- 注文の作成、取得、更新、削除
- 注文ステータス管理 (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- 注文作成時に `OrderCreatedEvent` を発行

### ✅ 3. Product ドメイン実装
**パッケージ**: `com.endo1116.combinationSpring.product`

#### 実装したクラス：
- `Product.java` - 商品エンティティ
- `ProductRepository.java` - JPA リポジトリ
- `ProductService.java` - ビジネスロジック（イベントリスナー含む）
- `ProductController.java` - REST API
- `package-info.java` - Spring Modulith モジュール定義

#### 機能：
- 商品の作成、取得、更新、削除
- 在庫管理（増減機能付き）
- `OrderCreatedEvent` を受け取り、自動的に在庫を更新

### ✅ 4. イベント駆動アーキテクチャ

#### 実装方法：
```java
// Order ドメイン - イベント発行
@Service
public class OrderService {
    @Transactional
    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        OrderCreatedEvent event = OrderCreatedEvent.from(savedOrder);
        eventPublisher.publishEvent(event);  // イベント発行
        return savedOrder;
    }
}

// Product ドメイン - イベント受信
@Service
public class ProductService {
    @ApplicationModuleListener  // Spring Modulith のアノテーション
    public void handleOrderCreated(OrderCreatedEvent event) {
        Product product = getProductById(event.getProductId());
        product.decreaseStock(event.getQuantity());  // 在庫を自動更新
        productRepository.save(product);
    }
}
```

#### Spring Modulith の特徴：
1. **イベントの永続化**: `event_publication` テーブルに自動保存
2. **非同期処理**: イベントリスナーは別スレッドで実行
3. **トランザクション管理**: イベント処理の信頼性を確保
4. **モジュール境界の検証**: 不適切な依存関係を自動検出

### ✅ 5. Next.js フロントエンド実装
**ディレクトリ**: `view/next-out/src/app/`

#### 実装したページ：
- `page.tsx` - ホームページ（システム説明）
- `products/page.tsx` - 商品管理ページ
- `orders/page.tsx` - 注文管理ページ

#### 機能：
- 商品の一覧表示、登録、削除
- 注文の一覧表示、登録
- 注文ステータスの更新
- リアルタイムで在庫数を確認
- レスポンシブデザイン（Tailwind CSS使用）

## 動作確認済み

### バックエンド
✅ アプリケーション起動成功  
✅ SQLiteデータベース接続成功  
✅ REST API動作確認  
✅ イベント駆動連携動作確認（在庫が自動更新されることを確認）

### テスト結果
```bash
# 商品登録
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"ノートPC","description":"高性能ノートパソコン","price":150000,"stockQuantity":10,"category":"電化製品"}'

# 結果: ID=1の商品が作成された（在庫=10）

# 注文作成
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"customerName":"田中太郎","quantity":3,"totalPrice":450000,"status":"PENDING"}'

# 結果: 注文が作成され、在庫が10→7に自動更新された ✅
```

### ログ確認
```
2025-10-23T07:07:05.149 INFO  OrderService: 注文を作成しました: 1
2025-10-23T07:07:05.153 INFO  OrderService: OrderCreatedEventを発行しました
2025-10-23T07:07:05.158 INFO  ProductService: OrderCreatedEventを受信しました
2025-10-23T07:07:05.167 INFO  ProductService: 商品の在庫を更新しました: 商品ID=1, 新しい在庫数=7
```

## アーキテクチャのメリット

### 1. モジュラーモノリス
- マイクロサービスの複雑さを避けつつ、モジュール化のメリットを享受
- 単一のデプロイメントユニットで管理が簡単
- 必要に応じて将来的にマイクロサービスへ分割可能

### 2. イベント駆動
- ドメイン間の疎結合を実現
- OrderドメインはProductドメインに直接依存しない
- 新しいドメインを追加しても既存コードへの影響が最小限

### 3. Spring Modulith
- モジュール境界の自動検証
- イベントの永続化と信頼性の向上
- テストのサポート（モジュール単位でのテストが可能）

## ファイル構成

```
combinationSpring/
├── build.gradle                          # Gradle設定（Spring Modulith依存関係）
├── src/main/
│   ├── java/com/endo1116/combinationSpring/
│   │   ├── order/                        # Orderドメイン
│   │   │   ├── Order.java
│   │   │   ├── OrderRepository.java
│   │   │   ├── OrderService.java
│   │   │   ├── OrderController.java
│   │   │   ├── OrderCreatedEvent.java
│   │   │   └── package-info.java
│   │   ├── product/                      # Productドメイン
│   │   │   ├── Product.java
│   │   │   ├── ProductRepository.java
│   │   │   ├── ProductService.java
│   │   │   ├── ProductController.java
│   │   │   └── package-info.java
│   │   └── CombinationSpringApplication.java
│   └── resources/
│       └── application.properties        # Turso (SQLite) 設定
├── view/next-out/                        # Next.jsフロントエンド
│   └── src/app/
│       ├── page.tsx                      # ホーム
│       ├── products/page.tsx             # 商品管理
│       └── orders/page.tsx               # 注文管理
├── data/                                 # SQLiteデータベース
│   └── app.db
└── README.md                             # 詳細なドキュメント
```

## 今後の拡張可能性

### 短期的な拡張
- [ ] 配送ドメインの追加
- [ ] 決済ドメインの追加
- [ ] ユーザー認証の実装

### 中期的な拡張
- [ ] Spring Modulith Observability の統合（メトリクス、トレーシング）
- [ ] イベントストアの実装（イベントソーシング）
- [ ] 非同期処理の最適化

### 長期的な拡張
- [ ] 特定のドメインをマイクロサービスとして分離
- [ ] イベント駆動マイクロサービスへの進化
- [ ] Kubernetes上でのデプロイメント

## 参考資料

- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/reference/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Next.js Documentation](https://nextjs.org/docs)

