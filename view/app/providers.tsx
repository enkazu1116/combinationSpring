"use client";

import { Provider } from "react-redux";
import { appStore } from "@/app/store/store";

// Redux Providerを切り出してレイアウトから利用できるようにする
export function AppProviders({ children }: { children: React.ReactNode }) {
  return <Provider store={appStore}>{children}</Provider>;
}


