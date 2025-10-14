"use client";

import { createSlice, PayloadAction } from "@reduxjs/toolkit";

// アプリ全体で共有するカウンタの状態
// - currentCount: 現在のカウント数
export type CounterState = {
  currentCount: number;
};

// 初期状態は0から開始
const initialState: CounterState = {
  currentCount: 0,
};

// カウンタ用スライス
// - incrementByAmount: 指定値で加算
// - reset: 0に戻す
export const counterSlice = createSlice({
  name: "counter",
  initialState,
  reducers: {
    incrementByAmount: (state, action: PayloadAction<number>) => {
      state.currentCount += action.payload;
    },
    reset: (state) => {
      state.currentCount = 0;
    },
  },
});

export const { incrementByAmount, reset } = counterSlice.actions;

export default counterSlice.reducer;


