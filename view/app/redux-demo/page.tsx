"use client";

import { useState } from "react";
import { useAppDispatch, useAppSelector } from "@/app/store/store";
import { incrementByAmount, reset } from "@/app/store/counterSlice";

// Reduxの基本的な使い方を示すサンプルページ
export default function ReduxDemoPage() {
  // 入力欄で増加値を管理するローカル状態
  const [incrementInputValue, setIncrementInputValue] = useState<string>("1");

  // Reduxのグローバル状態から現在のカウントを取得
  const globalCurrentCount = useAppSelector((state) => state.counter.currentCount);

  // ReduxへActionを送るためのディスパッチ関数
  const dispatch = useAppDispatch();

  const handleIncrement = () => {
    const parsed = Number(incrementInputValue);
    if (!Number.isFinite(parsed)) return;
    dispatch(incrementByAmount(parsed));
  };

  const handleReset = () => dispatch(reset());

  return (
    <main className="flex min-h-dvh flex-col items-center justify-center gap-6 p-6">
      <h1 className="text-2xl font-bold">Redux デモ</h1>
      <p className="text-muted-foreground">現在のカウント: {globalCurrentCount}</p>

      <div className="flex items-center gap-3">
        <input
          aria-label="増加値"
          value={incrementInputValue}
          onChange={(e) => setIncrementInputValue(e.target.value)}
          className="border rounded px-3 py-2"
          placeholder="増やす数値"
          inputMode="numeric"
        />
        <button onClick={handleIncrement} className="border rounded px-3 py-2">
          指定値で増加
        </button>
        <button onClick={handleReset} className="border rounded px-3 py-2">
          リセット
        </button>
      </div>
    </main>
  );
}


