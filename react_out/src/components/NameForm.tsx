import React, { useState } from "react";

type FormState = {
    email: string;
    password: string;
    remeber: boolean;
}

export default function NameForm() {

    const [form, setForm] = useState<FormState>({
        email: "",
        password: "",
        remeber: false,
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = e.target;
        setForm(prev => ({
            ...prev,
            [name]:type === "checkbox" ? checked :value,
        }));
    }

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        alert(`送信： ${JSON.stringify(form, null, 2)}`);
    }

    return (
        <form onSubmit={handleSubmit}>
            <input 
                type="email"
                name="email"
                value={form.email} 
                onChange={handleChange} />
            <button type="submit">送信</button>
        </form>
    );
}