import { createRoot } from "react-dom/client";
import HeaderTitle from "./HeaderTitle";

const root = createRoot(
  document.getElementById('root') as HTMLElement
)

root.render(
  <>
    <HeaderTitle />
  </>
)