import { Link } from "react-router-dom";

export default function ForgotPasswordLink() {
    return (
        <div style={{ marginTop: "8px" }}>
            <Link to="/forgot-password">パスワードをお忘れですか？</Link>
        </div>
    )
}