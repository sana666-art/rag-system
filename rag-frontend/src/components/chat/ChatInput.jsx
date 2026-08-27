import { useEffect, useRef, useState } from "react";
import { ArrowUp, Plus, Square } from "lucide-react";
import { AnimatePresence, motion } from "framer-motion";
import toast from "react-hot-toast";
import Tooltip from "../ui/Tooltip";

export default function ChatInput({ onSend, onStop, disabled, loading = false }) {

    const [value, setValue] = useState("");
    const textareaRef = useRef(null);

    const canSend = value.trim().length > 0 && !disabled && !loading;

    useEffect(() => {
        textareaRef.current?.focus();
    }, []);

    const handleSubmit = () => {

        if (!canSend) return;

        onSend(value.trim());

        setValue("");

        if (textareaRef.current) {
            textareaRef.current.style.height = "auto";
        }

        textareaRef.current?.focus();
    };

    const handleKeyDown = (event) => {

        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            handleSubmit();
        }
    };

    const autoResize = () => {

        const el = textareaRef.current;

        if (!el) return;

        el.style.height = "auto";

        el.style.height = `${Math.min(el.scrollHeight, 200)}px`;
    };

    return (
        <div className="shrink-0 px-3 pb-3 pt-2 md:px-4 md:pb-4">
            <div className="mx-auto w-full max-w-3xl">
                <div className="flex items-end gap-1.5 rounded-3xl border border-gray-300 bg-white px-3 py-3 shadow-sm transition focus-within:border-gray-400 focus-within:shadow-md">
                    <Tooltip content="Attachments coming soon">
                        <button
                            type="button"
                            aria-label="Attach"
                            onClick={() => toast("Attachments are coming soon", { icon: "📎" })}
                            className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-gray-500 transition hover:bg-gray-100 hover:text-gray-900"
                        >
                            <Plus className="h-4 w-4" />
                        </button>
                    </Tooltip>

                    <textarea
                        ref={textareaRef}
                        value={value}
                        onChange={(event) => {
                            setValue(event.target.value);
                            autoResize();
                        }}
                        onKeyDown={handleKeyDown}
                        placeholder={
                            loading
                                ? "RAG Assistant is thinking..."
                                : "Ask anything about your portfolio..."
                        }
                        rows={1}
                        className="max-h-[200px] min-h-[24px] flex-1 resize-none overflow-hidden bg-transparent text-base leading-relaxed text-gray-900 outline-none placeholder:text-gray-400"
                        style={{ overflowY: value.split("\n").length > 8 || value.length > 300 ? "auto" : "hidden" }}
                    />

                    <AnimatePresence>
                        {loading ? (
                            <motion.button
                                type="button"
                                onClick={onStop}
                                initial={{ opacity: 0, scale: 0.6 }}
                                animate={{ opacity: 1, scale: 1 }}
                                exit={{ opacity: 0, scale: 0.6 }}
                                transition={{ duration: 0.15 }}
                                className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-gray-900 text-white transition hover:bg-black"
                                aria-label="Stop generating"
                            >
                                <Square className="h-3.5 w-3.5 fill-current" />
                            </motion.button>
                        ) : (
                            canSend && (
                                <motion.button
                                    type="button"
                                    onClick={handleSubmit}
                                    initial={{ opacity: 0, scale: 0.6 }}
                                    animate={{ opacity: 1, scale: 1 }}
                                    exit={{ opacity: 0, scale: 0.6 }}
                                    transition={{ duration: 0.15 }}
                                    className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-gray-900 text-white transition hover:bg-black"
                                    aria-label="Send message"
                                >
                                    <ArrowUp className="h-4 w-4" />
                                </motion.button>
                            )
                        )}
                    </AnimatePresence>
                </div>

                <p className="mt-2 text-center text-xs text-gray-400">
                    RAG Assistant can make mistakes. Check important info.
                </p>
            </div>
        </div>
    );
}
