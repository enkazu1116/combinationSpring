"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { CosmicBackground } from "@/app/components/CosmicBackground";

/*
  トップページ（Cosmoテーマ + shadcn/ui）
  - 変更点コメント:
    1) 背景: CosmicBackgroundで星空/流星アニメを追加
    2) 配色: globals.cssにcosmo-gradient等のユーティリティを追加
    3) UI: shadcn/uiのButton/Cardを使用し、余白・視認性を最適化
*/
export default function HomePage() {
  return (
    <main className="relative min-h-dvh overflow-hidden">
      <CosmicBackground />

      {/* Hero セクション */}
      <section className="container mx-auto flex max-w-5xl flex-col items-center gap-6 px-6 pb-20 pt-28 text-center">
        <h1 className="cosmo-glow text-balance text-4xl font-bold sm:text-5xl md:text-6xl">
          宇宙を感じるUI体験
        </h1>
        <p className="text-pretty text-muted-foreground">
          shadcn/ui をコスモテーマにカスタマイズ。星の瞬きと流星が、静的なUIに生命感を与えます。
        </p>
        <div className="flex flex-wrap items-center justify-center gap-3">
          <Button asChild size="lg" className="backdrop-blur">
            <Link href="/redux-demo">Redux デモを見る</Link>
          </Button>
          <Button variant="secondary" size="lg" className="backdrop-blur">
            <Link href="https://nextjs.org" target="_blank" rel="noreferrer">Next.js</Link>
          </Button>
        </div>
      </section>

      {/* Features セクション */}
      <section className="container mx-auto grid max-w-5xl grid-cols-1 gap-6 px-6 pb-28 sm:grid-cols-2">
        <Card className="backdrop-blur">
          <CardHeader>
            <CardTitle>洗練されたUI</CardTitle>
            <CardDescription>shadcn/uiのコンポーネントに宇宙のグローを追加</CardDescription>
          </CardHeader>
          <CardContent>
            <p>微かな光彩と濃淡のレイヤーで、奥行きのある表現に。</p>
          </CardContent>
        </Card>

        <Card className="backdrop-blur">
          <CardHeader>
            <CardTitle>動きのある背景</CardTitle>
            <CardDescription>星の瞬きと流星が緩やかに流れる演出</CardDescription>
          </CardHeader>
          <CardContent>
            <p>視覚ノイズを抑えつつ、ほどよい生命感を付与。</p>
          </CardContent>
        </Card>

        <Card className="backdrop-blur sm:col-span-2">
          <CardHeader>
            <CardTitle>型安全と状態管理</CardTitle>
            <CardDescription>Redux Toolkit + 型付きHooksで堅牢に</CardDescription>
          </CardHeader>
          <CardContent>
            <p>アプリの拡張に備えた構造を標準の慣習で構築。</p>
          </CardContent>
        </Card>
      </section>
    </main>
  );
}
