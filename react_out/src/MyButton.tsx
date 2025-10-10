
type MyButtonProps = {
    onClick: (event: React.MouseEvent<HTMLInputElement>) => void
}

export default function MyButton({ onClick }: MyButtonProps) {

    return <input type="button" value="クリック" onClick={onClick} />;
}