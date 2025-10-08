import type { ReactElement, ReactNode } from "react"
import React from "react";

type Props = {
    children: ReactNode;
}

export default function HeaderTitle({ children }: Props) {
    const childrenArray = Array.isArray(children) ? children : [children];
    const title = childrenArray.find(
        (elem): elem is ReactElement =>
            React.isValidElement(elem) && elem.key === 'title'
    );

    return <h1>{title}</h1>;
}