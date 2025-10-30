"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";

// Next.js App Router でページを追加する例
// 1. app/sample-ui ディレクトリを作成し、page.tsx を用意すると自動で /sample-ui ルートが生成されます。
// 2. 各UIパーツを React コンポーネントとして宣言し、Tailwind CSS のユーティリティクラスでスタイルを付与します。
// 3. UI構成を段階的にコメントで示し、どの要素がどのような役割を持つかを可視化します。
export default function SampleUILayout() {
  return (
    <div className="mx-auto flex min-h-dvh w-full max-w-5xl flex-col gap-10 px-6 py-16">
      {/* Step 1: ページ全体の余白・レイアウトを決める */}
      <header className="flex flex-col gap-4 text-center">
        <p className="text-sm font-semibold tracking-wide text-primary">Next.js + Tailwind UI Sample</p>
        <h1 className="text-balance text-4xl font-bold md:text-5xl">段階的に組み立てるインターフェース</h1>
        <p className="text-muted-foreground">
          App Router で新しいページを作成し、情報カード・ステップガイド・CTA を配置するまでの流れをコメント付きでまとめました。
        </p>
        <div className="flex flex-wrap items-center justify-center gap-3">
          <Button asChild size="lg">
            <Link href="/">トップへ戻る</Link>
          </Button>
          <Button asChild variant="outline" size="lg">
            <Link href="https://nextjs.org/docs/app">App Router Docs</Link>
          </Button>
        </div>
      </header>

      <Separator />

      {/* Step 2: 3つの特徴カードで機能を説明するセクション */}
      <section className="grid gap-4 md:grid-cols-3">
        <FeatureCard
          step="01"
          title="ルートの作成"
          description="app/ 配下にディレクトリを作るだけで、その名前がルートになります。"
          details="例: app/sample-ui/page.tsx → /sample-ui"
        />
        <FeatureCard
          step="02"
          title="UIコンポーネント"
          description="shadcn/ui の Card などを利用すると統一されたデザインを素早く構築できます。"
          details="CardHeader・CardContent を使うと情報構造が明確になります。"
        />
        <FeatureCard
          step="03"
          title="Tailwindで調整"
          description="flex や grid などのユーティリティクラスで余白やレイアウトを細かく制御します。"
          details="レスポンシブ対応は md: や lg: の接頭辞で指定します。"
        />
      </section>

      {/* Step 3: 手順書をタイムライン形式で表現するセクション */}
      <section className="rounded-xl border bg-background/60 p-6 backdrop-blur">
        <h2 className="text-2xl font-semibold">実装の流れ</h2>
        <p className="text-sm text-muted-foreground">最小の構成でページを組み立てるための4ステップ</p>

        <ol className="mt-6 space-y-5">
          <TimelineStep
            title="ディレクトリとpage.tsxを作る"
            body={`Next.js App Routerでは、app以下のフォルダ構成がそのままURLになります。page.tsxはページを描画する最小単位です。`}
          />
          <TimelineStep
            title="UIに使うコンポーネントをインポート"
            body="shadcn/uiなどのUIライブラリから必要な部品(Button, Cardなど)を取り込みます。Tailwindのクラスを併用して見た目を整えます。"
          />
          <TimelineStep
            title="セクションを分割してレイアウト"
            body="ヘッダー・特徴紹介・タイムラインなど目的ごとにセクションを区切り、flexやgridで配置を調整します。"
          />
          <TimelineStep
            title="文言とリンクを配置して完成"
            body="CTAボタンやドキュメントリンクを配置し、利用者が次に取るアクションを提示します。"
          />
        </ol>
      </section>

      {/* Step 4: まとめと次のアクションを案内するフッター */}
      <footer className="rounded-xl border bg-primary/5 p-6 text-center">
        <h3 className="text-2xl font-semibold">このページを出発点にカスタマイズ</h3>
        <p className="text-muted-foreground">
          UIの骨格が整えば、配色やアニメーション、状態管理などを段階的に追加できます。
        </p>
        <div className="mt-4 flex flex-wrap justify-center gap-3">
          <Button asChild size="lg">
            <Link href="/redux-demo">Reduxデモを見る</Link>
          </Button>
          <Button asChild variant="secondary" size="lg">
            <Link href="https://tailwindcss.com/docs">Tailwind Docs</Link>
          </Button>
        </div>
      </footer>
    </div>
  );
}

// --- 補助コンポーネント ---
// FeatureCard: 特徴カードを量産するためのプレゼンテーションコンポーネント
function FeatureCard({
  step,
  title,
  description,
  details,
}: {
  step: string;
  title: string;
  description: string;
  details: string;
}) {
  return (
    <Card className="flex h-full flex-col border-primary/30">
      <CardHeader>
        <p className="text-xs font-semibold uppercase tracking-[0.3em] text-primary">Step {step}</p>
        <CardTitle>{title}</CardTitle>
        <CardDescription>{description}</CardDescription>
      </CardHeader>
      <CardContent className="mt-auto text-sm text-muted-foreground">{details}</CardContent>
    </Card>
  );
}

// TimelineStep: 箇条書きではなくカード風に表示するためのコンポーネント
function TimelineStep({ title, body }: { title: string; body: string }) {
  return (
    <li className="rounded-lg border bg-background/80 p-4 shadow-sm">
      <h3 className="text-lg font-semibold">{title}</h3>
      <p className="text-sm text-muted-foreground">{body}</p>
    </li>
  );
}
