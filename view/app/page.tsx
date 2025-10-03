import { useState } from "react";

export default function Home() {
  const [ count, setCount ] = useState(0);
  const handleClick = () => setCount(count + 1);

  return (
    <>
      <button onClick={handleClick}>
        カウント
      </button>
      <p>{count}回クリックされました</p>
    </>
  );
}
