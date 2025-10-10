import type React from "react";

type TextInputProps = {
    value: string;
    setValue: React.Dispatch<React.SetStateAction<string>>;
    label?: string;
    type?: "text" | "password";
}

export default function TextInput({ value, setValue, label, type = "text"}: TextInputProps) {
    return (
        <label>
            {label}
            <input type={type} value={value} onChange={e => setValue(e.target.value)} />
        </label>
    );
}