import type React from "react";

type SubmitButtonProps = {
    label?: string;
    onClick: (event: React.MouseEvent<HTMLButtonElement>) => void;
    type?: "button" | "submit" | "reset";
}

export default function SubmitButton({
    label = "送信",
    onClick,
    type = "button"
}: SubmitButtonProps) {
    return <button type={type} onClick={onClick}>{label}</button>;    
}