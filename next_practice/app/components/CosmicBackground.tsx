"use client";

import React from "react";

/*
  CosmicBackground
  - 目的: コスモテーマの動きのある背景を提供
  - 内容: コスモグラデ + 星の瞬き + ランダムな流星
  - カスタマイズ: colors/animationはglobals.cssで定義（cosmo-gradient/twinkle/meteor）
*/
export function CosmicBackground() {
  return (
    <div className="pointer-events-none fixed inset-0 -z-10 cosmo-gradient">
      {/* 星のレイヤー1: 細かい星 */}
      <div
        className="absolute inset-0 twinkle"
        style={{
          backgroundImage:
            "radial-gradient(1px 1px at 20% 30%, white 60%, transparent)," +
            "radial-gradient(1px 1px at 40% 70%, white 60%, transparent)," +
            "radial-gradient(1px 1px at 80% 20%, white 60%, transparent)," +
            "radial-gradient(1px 1px at 65% 50%, white 60%, transparent)",
          opacity: 0.6,
        }}
      />

      {/* 星のレイヤー2: 大きめの星（淡いグロー） */}
      <div
        className="absolute inset-0"
        style={{
          backgroundImage:
            "radial-gradient(2px 2px at 15% 80%, oklch(0.95 0.05 280) 70%, transparent)," +
            "radial-gradient(2px 2px at 85% 60%, oklch(0.97 0.06 240) 70%, transparent)",
          filter: "drop-shadow(0 0 6px oklch(0.98 0.08 260))",
          opacity: 0.5,
        }}
      />

      {/* 流星: ランダム配置を複数用意 */}
      {Array.from({ length: 4 }).map((_, index) => (
        <div
          key={index}
          className="meteor absolute"
          style={{
            top: `${10 + index * 20}%`,
            right: `${-10 + index * 5}%`,
            animationDelay: `${index * 1.8}s`,
          }}
        />
      ))}
    </div>
  );
}


