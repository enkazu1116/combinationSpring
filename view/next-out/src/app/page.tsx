import Image from "next/image";
import Link from "next/link";

/*
  Next.js 13+ App Router の基本構造と解説
  ===========================================
  
  1. App Router とは？
  - Next.js 13から導入された新しいルーティングシステム
  - pages/ ディレクトリの代わりに app/ ディレクトリを使用
  - ファイルベースのルーティング + レイアウト機能を提供
  
  2. ディレクトリ構造の意味
  - app/page.tsx → ルートパス（/）のページ
  - app/about/page.tsx → /about のページ
  - app/layout.tsx → 全ページ共通のレイアウト
  - app/loading.tsx → ローディングUI
  - app/error.tsx → エラーUI
  
  3. このファイルの役割
  - デフォルトエクスポートされたコンポーネントがページコンテンツ
  - サーバーコンポーネントとして動作（デフォルト）
  - SEO最適化とパフォーマンス向上が自動適用
*/
export default function Home() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-8">
      <div className="max-w-4xl mx-auto">
        {/* メインヘッダー */}
        <header className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Next.js 13+ App Router デモ
          </h1>
          <p className="text-lg text-gray-600">
            React フレームワークの最新機能を体験しよう
          </p>
        </header>

        {/* 機能紹介セクション */}
        <section className="grid md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
          <FeatureCard
            title="App Router"
            description="ファイルベースのルーティングシステム"
            icon="📁"
          />
          <FeatureCard
            title="Server Components"
            description="サーバーサイドでのレンダリング"
            icon="⚡"
          />
          <FeatureCard
            title="Image Optimization"
            description="自動的な画像最適化"
            icon="🖼️"
          />
        </section>

        {/* Next.js Image コンポーネントのデモ */}
        <section className="bg-white rounded-lg shadow-lg p-6 mb-8">
          <h2 className="text-2xl font-semibold mb-4">Next.js Image コンポーネント</h2>
          <div className="flex flex-col md:flex-row gap-4 items-center">
            <div className="relative w-64 h-48">
              {/* 
                Next.js Image コンポーネントの特徴：
                - 自動的な遅延読み込み（Lazy Loading）
                - WebP/AVIF 形式への自動変換
                - レスポンシブ画像の自動生成
                - レイアウトシフトの防止
              */}
              <Image
                src="https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400&h=300&fit=crop"
                alt="Next.js ロゴのデモ画像"
                fill
                className="object-cover rounded-lg"
                sizes="(max-width: 768px) 100vw, 50vw"
                priority // 重要な画像は優先読み込み
              />
            </div>
            <div className="flex-1">
              <h3 className="text-lg font-medium mb-2">最適化された画像表示</h3>
              <ul className="text-sm text-gray-600 space-y-1">
                <li>• 自動的な遅延読み込み</li>
                <li>• モダンな画像形式への変換</li>
                <li>• レスポンシブ対応</li>
                <li>• レイアウトシフト防止</li>
              </ul>
            </div>
          </div>
        </section>

        {/* ナビゲーションリンク */}
        <section className="text-center">
          <h2 className="text-2xl font-semibold mb-4">他のページを探索</h2>
          <div className="flex flex-wrap justify-center gap-4">
            <Link 
              href="/about" 
              className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors"
            >
              アバウトページへ
            </Link>
            <Link 
              href="/api-demo" 
              className="bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 transition-colors"
            >
              API ルートデモ
            </Link>
          </div>
        </section>
      </div>
    </main>
  );
}

/*
  再利用可能なコンポーネント
  =========================
  
  React コンポーネントの基本パターン：
  - プロパティ（props）でデータを受け取る
  - JSX で UI を定義
  - TypeScript で型安全性を確保
*/
function FeatureCard({ 
  title, 
  description, 
  icon 
}: { 
  title: string; 
  description: string; 
  icon: string; 
}) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="text-3xl mb-3">{icon}</div>
      <h3 className="text-xl font-semibold mb-2">{title}</h3>
      <p className="text-gray-600">{description}</p>
    </div>
  );
}
