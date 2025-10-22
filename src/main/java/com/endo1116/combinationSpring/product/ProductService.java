package com.endo1116.combinationSpring.product;

import com.endo1116.combinationSpring.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 商品管理サービス
 * 
 * Redis活用：
 * 1. キャッシュ - 頻繁に読み取られる商品情報をキャッシュ
 * 2. 分散ロック - 在庫更新時の競合を防止
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;
    
    @Transactional
    @CachePut(value = "products", key = "#result.id")
    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        log.info("商品を作成しました: {}", savedProduct.getId());
        return savedProduct;
    }
    
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * 商品情報をキャッシュから取得
     * キャッシュがない場合はDBから取得してキャッシュに保存
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        log.info("DBから商品を取得します: {}", id);
        return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }
    
    /**
     * 商品更新時はキャッシュも更新
     */
    @Transactional
    @CachePut(value = "products", key = "#id")
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: " + id));
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        Product updatedProduct = productRepository.save(product);
        log.info("商品を更新しました: {}", id);
        return updatedProduct;
    }
    
    /**
     * 商品削除時はキャッシュからも削除
     */
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        log.info("商品を削除しました: {}", id);
    }
    
    /**
     * 注文作成イベントを受け取り、在庫を減らす
     * 
     * Redisの分散ロックを使用して、同時に複数の注文が来ても在庫の整合性を保証
     * 
     * 処理フロー：
     * 1. 商品IDに基づいた分散ロックを取得（最大10秒待機）
     * 2. ロックを取得できたら在庫を更新
     * 3. キャッシュをクリア
     * 4. ロックを解放
     */
    @ApplicationModuleListener
    @CacheEvict(value = "products", key = "#event.productId")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("OrderCreatedEventを受信しました: {}", event);
        
        String lockKey = "product:stock:lock:" + event.getProductId();
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 分散ロックを取得（最大10秒待機、ロック保持時間30秒）
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            
            if (!isLocked) {
                throw new IllegalStateException("在庫更新のロック取得に失敗しました: " + event.getProductId());
            }
            
            log.info("分散ロックを取得しました: {}", lockKey);
            
            // キャッシュをバイパスしてDBから最新データを取得
            Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: " + event.getProductId()));
            
            product.decreaseStock(event.getQuantity());
            productRepository.save(product);
            
            log.info("商品の在庫を更新しました: 商品ID={}, 新しい在庫数={}", 
                product.getId(), product.getStockQuantity());
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("ロック取得中に中断されました: {}", e.getMessage());
            throw new IllegalStateException("在庫更新処理が中断されました", e);
        } catch (Exception e) {
            log.error("在庫更新に失敗しました: {}", e.getMessage());
            throw e;
        } finally {
            // ロックを保持している場合のみ解放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("分散ロックを解放しました: {}", lockKey);
            }
        }
    }
    
    /**
     * 在庫チェック（Redis経由）
     * キャッシュから在庫を確認することで、DBへの負荷を軽減
     */
    public boolean checkStock(Long productId, Integer requiredQuantity) {
        Product product = getProductById(productId);
        return product.getStockQuantity() >= requiredQuantity;
    }
}

