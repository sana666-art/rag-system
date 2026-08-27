import { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowUp } from "lucide-react";

const SUGGESTIONS = [
    "What stocks do I own?",
    "Show my transactions",
    "What are my deposits?",
];

const GUEST_DAILY_LIMIT = 20;

export default function DemoComposer() {

    const navigate = useNavigate();
    const [value, setValue] = useState("");
    const textareaRef = useRef(null);

    const canSend = value.trim().length > 0;

    const submit = (question) => {
        const text = (question ?? value).trim();
        if (!text) return;
        navigate("/guest-chat", { state: { question: text } });
    };

    const handleKeyDown = (event) => {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            submit();
        }
    };

    const autoResize = () => {
        const el = textareaRef.current;
        if (!el) return;
        el.style.height = "auto";
        el.style.height = `${Math.min(el.scrollHeight, 200)}px`;
    };

    return (
        <div className="mx-auto mt-10 w-full max-w-2xl">
            <div className="flex items-end gap-2 rounded-3xl border border-gray-300 bg-white px-4 py-3 text-left shadow-sm transition focus-within:border-gray-400 focus-within:shadow-md">
                <textarea
                    ref={textareaRef}
                    value={value}
                    onChange={(event) => {
                        setValue(event.target.value);
                        autoResize();
                    }}
                    onKeyDown={handleKeyDown}
                    placeholder="Ask anything about your portfolio..."
                    rows={1}
                    className="max-h-40 flex-1 resize-none bg-transparent text-base text-gray-900 outline-none placeholder:text-gray-400"
                />

                <button
                    type="button"
                    onClick={() => submit()}
                    disabled={!canSend}
                    aria-label="Ask"
                    className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-gray-900 text-white transition hover:bg-black disabled:cursor-not-allowed disabled:opacity-40"
                >
                    <ArrowUp className="h-4 w-4" />
                </button>
            </div>

            <div className="mt-4 flex flex-wrap items-center justify-center gap-2.5">
                {SUGGESTIONS.map((suggestion) => (
                    <button
                        key={suggestion}
                        type="button"
                        onClick={() => submit(suggestion)}
                        className="rounded-full border border-gray-300 px-4 py-2 text-sm font-medium text-gray-600 transition hover:border-gray-400 hover:bg-gray-50 hover:text-gray-900"
                    >
                        {suggestion}
                    </button>
                ))}
            </div>

            <p className="mt-4 text-center text-xs text-gray-400">
                {GUEST_DAILY_LIMIT} free questions a day · no sign-up needed
            </p>
        </div>
    );
}
