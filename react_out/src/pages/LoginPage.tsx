import { useState } from "react";
import TextInput from "../components/TextInput";
import SubmitButton from "../components/SubmitButton";
import ForgotPasswordLink from "../components/ForgotPasswordLink";

export default function LoginPage() {
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");

    const handleSubmit = () => {
        console.log({ email, password });
    }
    return (
        <>
            <TextInput value={email} setValue={setEmail} label="メール" />
            <TextInput value={password} setValue={setPassword} label="パスワード" type="password" />
            <SubmitButton label="ログイン" onClick={handleSubmit} type="submit" />
            <ForgotPasswordLink />
        </> 
    );
}