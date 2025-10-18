"use client";

import { configureStore } from "@reduxjs/toolkit";
import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux";
import counterReducer from "@/app/store/counterSlice";

// Reduxストアの作成
export const appStore = configureStore({
  reducer: {
    counter: counterReducer,
  },
  // ここでは特別なミドルウェアは追加せず、Toolkitのデフォルトを使用
});

// ルートの状態型とディスパッチ型をエクスポート
export type RootState = ReturnType<typeof appStore.getState>;
export type AppDispatch = typeof appStore.dispatch;

// 型安全なHooks
export const useAppDispatch: () => AppDispatch = useDispatch;
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;


