/**
 * Product Domain Module
 * 
 * 商品管理ドメイン
 * - 商品の作成、更新、削除
 * - 在庫管理
 * - 注文作成イベントを受け取り在庫を更新
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Product Domain",
    allowedDependencies = {}
)
package com.endo1116.combinationSpring.product;

