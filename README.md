# Spring Modulith + Turso DB + Next.js デモアプリケーション

## 概要

このアプリケーションは **Spring Modulith** を使用したモジュラーモノリスアーキテクチャのデモです。
イベント駆動アーキテクチャにより、ドメイン間の疎結合な連携を実現しています。

### 技術スタック

- **バックエンド**
  - Spring Boot 3.4.2
  - Spring Modulith 1.3.1
  - Spring Data JPA
  - Turso (SQLite) Database
  - Apache Kafka (Outboxパターン)
  - Redis (キャッシュ & 分散ロック)
  - Java 21

- **フロントエンド**
  - Next.js 15.5
  - React 19
  - TypeScript
  - Tailwind CSS

- **インフラ**
  - Docker & Docker Compose
  - Kafka + Zookeeper
  - Redis
  - Kafka UI (管理画面)
  - Redis Commander (管理画面)

## アーキテクチャ

### ドメイン構成

#### 1. Order ドメイン (`com.endo1116.combinationSpring.order`)
- 注文の作成、更新、削除
- 注文ステータス管理 (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- 注文作成時に `OrderCreatedEvent` を発行

#### 2. Product ドメイン (`com.endo1116.combinationSpring.product`)
- 商品の作成、更新、削除
- 在庫管理
- `OrderCreatedEvent` を受け取り、自動的に在庫を更新

### イベント駆動アーキテクチャ

```
Order ドメイン                    Product ドメイン
    |                                  |
    | 1. 注文作成                        |
    |                                  |
    | 2. OrderCreatedEvent 発行         |
    |--------------------------------->|
                                       |
                                       | 3. イベント受信
                                       | 4. 在庫更新
```

`ProductService` の `handleOrderCreated` メソッドが `@ApplicationModuleListener` アノテーションにより、
`OrderCreatedEvent` を自動的に受け取り、商品の在庫を減らします。

## セットアップ

### 前提条件

- Docker & Docker Compose
- Java 21
- Node.js 18+ (フロントエンド使用時)

### 1. Docker環境の起動（Kafka & Redis）

```bash
# Kafka、Redis、管理UIを起動
docker-compose up -d

# 起動確認
docker-compose ps

# ログ確認
docker-compose logs -f
```

起動するサービス：
- Kafka: `localhost:9092`
- Redis: `localhost:6379`
- Kafka UI: `http://localhost:8090`
- Redis Commander: `http://localhost:8091`

### 2. バックエンドのセットアップ

1. **データベース用のディレクトリを作成**
   ```bash
   mkdir -p data
   ```

2. **依存関係のインストールとビルド**
   ```bash
   ./gradlew clean build -x test
   ```

3. **アプリケーションの起動**
   ```bash
   ./gradlew bootRun
   ```
   
   または、JARファイルを直接実行：
   ```bash
   java -jar build/libs/combinationSpring-0.0.1-SNAPSHOT.jar
   ```
   
   サーバーは `http://localhost:8080` で起動します。

### 3. フロントエンドのセットアップ（オプション）

1. **ディレクトリ移動**
   ```bash
   cd view/next-out
   ```

2. **依存関係のインストール**
   ```bash
   npm install
   ```

3. **開発サーバーの起動**
   ```bash
   npm run dev
   ```
   
   フロントエンドは `http://localhost:3000` で起動します。

## API エンドポイント

### Product API

- `GET /api/products` - 全商品取得
- `GET /api/products/{id}` - 商品詳細取得
- `GET /api/products/category/{category}` - カテゴリ別商品取得
- `GET /api/products/search?name={name}` - 商品名検索
- `POST /api/products` - 商品作成
- `PUT /api/products/{id}` - 商品更新
- `DELETE /api/products/{id}` - 商品削除

### Order API

- `GET /api/orders` - 全注文取得
- `GET /api/orders/{id}` - 注文詳細取得
- `GET /api/orders/customer/{customerName}` - 顧客別注文取得
- `POST /api/orders` - 注文作成
- `PATCH /api/orders/{id}/status?status={status}` - 注文ステータス更新
- `DELETE /api/orders/{id}` - 注文削除

## 使い方

### 方法1: フロントエンドとバックエンドの両方を使用

1. **バックエンドを起動**（前述の手順）

2. **フロントエンドを起動**
   ```bash
   cd view/next-out
   npm install
   npm run dev
   ```

3. **ブラウザで `http://localhost:3000` にアクセス**

4. **商品の作成**
   - 「商品管理」ページで新規商品を追加
   - 商品名、価格、在庫数などを入力

5. **注文の作成**
   - 「注文管理」ページで新規注文を作成
   - 商品を選択し、顧客名と数量を入力
   - 注文が作成されると、自動的に商品の在庫が減ります

### 方法2: curlでAPIを直接テスト

1. **商品を作成**
   ```bash
   curl -X POST http://localhost:8080/api/products \
     -H "Content-Type: application/json" \
     -d '{"name":"ノートPC","description":"高性能ノートパソコン","price":150000,"stockQuantity":10,"category":"電化製品"}'
   ```

2. **商品一覧を取得**
   ```bash
   curl http://localhost:8080/api/products
   ```

3. **注文を作成（在庫が自動的に減ります）**
   ```bash
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -d '{"productId":1,"customerName":"田中太郎","quantity":3,"totalPrice":450000,"status":"PENDING"}'
   ```

4. **商品の在庫を確認（10→7に減っているはず）**
   ```bash
   curl http://localhost:8080/api/products/1
   ```

5. **イベント駆動連携の確認**
   - バックエンドのログで `OrderCreatedEvent` の発行と受信を確認できます
   - `event_publication` テーブルにイベントが永続化されます（Spring Modulithの機能）

## プロジェクト構造

```
combinationSpring/
├── src/main/java/com/endo1116/combinationSpring/
│   ├── order/                    # Order ドメイン
│   │   ├── Order.java
│   │   ├── OrderRepository.java
│   │   ├── OrderService.java
│   │   ├── OrderController.java
│   │   ├── OrderCreatedEvent.java
│   │   └── package-info.java
│   ├── product/                  # Product ドメイン
│   │   ├── Product.java
│   │   ├── ProductRepository.java
│   │   ├── ProductService.java
│   │   ├── ProductController.java
│   │   └── package-info.java
│   └── CombinationSpringApplication.java
├── view/next-out/                # Next.js フロントエンド
│   └── src/app/
│       ├── page.tsx              # ホームページ
│       ├── products/page.tsx     # 商品管理ページ
│       └── orders/page.tsx       # 注文管理ページ
├── build.gradle
└── README.md
```

## 主要機能

### 1. Spring Modulith アーキテクチャ

- **モジュール境界の明確化**: 各ドメインは独立したパッケージ
- **イベント駆動**: `@ApplicationModuleListener` による疎結合
- **モジュール検証**: 不適切な依存関係を自動検出

### 2. Kafka Outboxパターン

- **トランザクション整合性**: イベントとDB更新を同一トランザクション内で処理
- **信頼性**: イベントをDBに永続化してから非同期でKafkaに発行
- **自動リトライ**: 発行失敗時の自動リトライ機能
- **外部連携**: Kafkaを通じた他システムへのイベント通知

```java
@Externalized("order.created::#{#this.orderId}")
public class OrderCreatedEvent implements Serializable {
    // 自動的にKafkaトピック "order.created" に発行される
}
```

### 3. Redis活用

#### キャッシュ機能
```java
@Cacheable(value = "products", key = "#id")
public Product getProductById(Long id) {
    // Redisからキャッシュを取得、なければDBから取得
}
```

#### 分散ロック
```java
RLock lock = redissonClient.getLock("product:stock:lock:" + productId);
lock.tryLock(10, 30, TimeUnit.SECONDS);
// 在庫更新の競合を防止
```

### 4. 管理UI

- **Kafka UI** (`http://localhost:8090`): Kafkaトピックとメッセージの管理
- **Redis Commander** (`http://localhost:8091`): Redisキーとキャッシュの管理

## 今後の拡張案

- [ ] 決済ドメインの追加
- [ ] 配送ドメインの追加
- [ ] 非同期イベント処理の実装
- [ ] イベントストアの実装
- [ ] モジュール間の依存関係ダイアグラムの自動生成
- [ ] Spring Modulith Observability の統合

## ライセンス

MIT License

