import { useEffect, useRef, useState } from "react";

export default function Home() {
  const [ count, setCount ] = useState(0);
  const handleClick = () => setCount(count + 1);

  const handleWheel = e => e.preventDefalut();
  const divRef = useRef(null);
  useEffect(() => {
    const div = divRef.current;
    div.addEventListener('wheel', handleWheel. { passive: false });
    return (() => {
      div.removeEventListner('wheel', handleWheel);
    });
  })

  return (
    <>
      <button onClick={handleClick}>
        カウント
      </button>
      <p>{count}回クリックされました</p>
    </>
  );
}
