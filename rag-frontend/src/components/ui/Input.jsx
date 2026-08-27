import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";

export default function Input({ label, error, type, ...props }) {

    const [visible, setVisible] = useState(false);
    const isPassword = type === "password";
    const inputType = isPassword && visible ? "text" : type;

    return (
        <div className="space-y-1">
            <label className="text-sm">
                {label}
            </label>

            <div className="relative">
                <input
                    type={inputType}
                    className={`w-full rounded-lg border p-3 ${isPassword ? "pr-10" : ""}`}
                    {...props}
                />

                {isPassword && (
                    <button
                        type="button"
                        tabIndex={-1}
                        onClick={() => setVisible((v) => !v)}
                        aria-label={visible ? "Hide password" : "Show password"}
                        className="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-600"
                    >
                        {visible ? <EyeOff size={18} /> : <Eye size={18} />}
                    </button>
                )}
            </div>

            {error && (
                <p className="text-red-500 text-sm">
                    {error}
                </p>
            )}
        </div>
    );
}
