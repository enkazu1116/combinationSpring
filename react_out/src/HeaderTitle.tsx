import type { ReactElement, ReactNode } from "react"
import React from "react";

type Props = {
    children: ReactNode[];
}

export default function HeaderTitle({ children }: Props) {
    const title = children.find(
        (elem): elem is ReactElement =>
            React.isValidElement(elem) && elem.key === 'title'
    );
}