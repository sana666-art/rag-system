import { useEffect, useRef, useState } from "react";
import { Navigate, useLocation, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { ArrowRight, ArrowUp, Lock } from "lucide-react";
import clsx from "clsx";
import { askGuest } from "../services/guestService";
import {
    createUserMessage,
    createAssistantMessage,
} from "../models/message";
import { useAuth } from "../hooks/useAuth";
import ChatMessage from "../components/chat/ChatMessage";
import Button from "../components/ui/Button";
import TypingIndicator from "../components/chat/TypingIndicator";

const SUGGESTIONS = [
    "What stocks do I own?",
    "How much have I deposited?",
    "What did I buy most recently?",
];

const GUEST_DAILY_LIMIT = 20;

const LOW_QUOTA_THRESHOLD = 5;

export default function GuestChat() {

    const navigate = useNavigate();
    const location = useLocation();
    const { isAuthenticated, loading: authLoading } = useAuth();

    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const [loading, setLoading] = useState(false);
    const [quota, setQuota] = useState({
        remaining: GUEST_DAILY_LIMIT,
        limit: GUEST_DAILY_LIMIT,
    });
    const sendingRef = useRef(false);
    const pendingQuestionRef = useRef(null);
    const handleSendRef = useRef(null);

    useEffect(() => {
        const question = location.state?.question;
        if (question) {
            pendingQuestionRef.current = question;
            window.history.replaceState({}, "");
        }
    }, [location.state]);

    useEffect(() => {
        const question = pendingQuestionRef.current;
        if (question) {
            pendingQuestionRef.current = null;
            handleSendRef.current?.(question);
        }
    }, []);

    if (authLoading) {
        return null;
    }

    if (isAuthenticated) {
        return <Navigate to="/dashboard/chat" replace />;
    }

    const exhausted = quota.remaining <= 0 && !loading;

    const showSignupCta = !exhausted && quota.remaining <= LOW_QUOTA_THRESHOLD;

    const handleSend = async (rawQuestion) => {

        const question = rawQuestion.trim();

        if (!question || loading || sendingRef.current || exhausted) return;

        sendingRef.current = true;

        const userMessage = createUserMessage(question);

        setMessages((previous) => [...previous, userMessage]);
        setInput("");
        setLoading(true);

        try {

            const response = await askGuest({ question });

            setQuota({
                remaining: response.remainingQuota,
                limit: response.quotaLimit,
            });

            const assistantMessage =
                createAssistantMessage(response.rag);

            setMessages((previous) => [...previous, assistantMessage]);

        } catch (error) {

            const message =
                error.response?.data?.message ||
                "Something went wrong. Please try again.";

            toast.error(message);

            if (
                error.response?.status === 429 &&
                message.includes("guest limit")
            ) {
                setQuota((q) => ({ ...q, remaining: 0 }));
            }

        } finally {
            setLoading(false);
            sendingRef.current = false;
        }
    };

    handleSendRef.current = handleSend;

    return (
        <div className="flex h-screen flex-col overflow-hidden bg-white">
            <header className="flex h-14 shrink-0 items-center justify-between border-b border-gray-200 bg-white px-5">
                <div className="flex items-center gap-2">
                    <div className="flex h-8 w-8 items-center justify-center rounded-full bg-gray-900 text-white">
                        <svg
                            viewBox="0 0 24 24"
                            className="h-4 w-4"
                            fill="none"
                            aria-hidden="true"
                        >
                            <path
                                d="M12 2 3.5 6.5v11L12 22l8.5-4.5v-11L12 2Z"
                                stroke="currentColor"
                                strokeWidth="1.5"
                            />
                            <path
                                d="M12 22v-9.5M3.5 6.5 12 12l8.5-5.5"
                                stroke="currentColor"
                                strokeWidth="1.5"
                            />
                        </svg>
                    </div>
                    <p className="text-sm font-semibold text-gray-900">
                        RAG Assistant
                    </p>
                    <span className="rounded-full bg-gray-100 px-2 py-0.5 text-[11px] font-medium text-gray-500">
                        Guest demo
                    </span>
                </div>

                <button
                    type="button"
                    onClick={() => navigate("/login")}
                    className="rounded-full border border-gray-300 px-4 py-1.5 text-sm font-medium text-gray-700 transition hover:bg-gray-50"
                >
                    Log in
                </button>
            </header>

            <main className="flex-1 overflow-y-auto px-4 py-6">
                <div className="mx-auto w-full max-w-3xl space-y-6">
                {messages.length === 0 && !loading ? (
                    <div className="flex h-full min-h-[55vh] flex-col items-center justify-center text-center">
                        <div className="mb-8 flex h-10 w-10 items-center justify-center rounded-full bg-gray-900 text-white">
                            <svg
                                viewBox="0 0 24 24"
                                className="h-5 w-5"
                                fill="none"
                                aria-hidden="true"
                            >
                                <path
                                    d="M12 2 3.5 6.5v11L12 22l8.5-4.5v-11L12 2Z"
                                    stroke="currentColor"
                                    strokeWidth="1.5"
                                />
                                <path
                                    d="M12 22v-9.5M3.5 6.5 12 12l8.5-5.5"
                                    stroke="currentColor"
                                    strokeWidth="1.5"
                                />
                            </svg>
                        </div>

                        <p className="text-2xl font-semibold tracking-tight text-gray-900">
                            How can I help you today?
                        </p>

                        <p className="mt-2 text-sm text-gray-500">
                            Try one of the questions below against the sample
                            portfolio.
                        </p>

                        <div className="mt-8 flex flex-col gap-2 sm:flex-row sm:flex-wrap sm:justify-center">
                            {SUGGESTIONS.map((suggestion) => (
                                <button
                                    key={suggestion}
                                    type="button"
                                    onClick={() => handleSend(suggestion)}
                                    className="rounded-full border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-100"
                                >
                                    {suggestion}
                                </button>
                            ))}
                        </div>
                    </div>
                ) : (
                    <>
                        {messages.map((message, index) => (
                            <div key={message.id} className="space-y-4">
                                <ChatMessage
                                    message={message}
                                    hideDeveloperPanel
                                    maxSources={4}
                                />

                                {showSignupCta &&
                                    message.role === "assistant" &&
                                    index === messages.length - 1 && (
                                        <div className="flex justify-center pt-1">
                                            <button
                                                type="button"
                                                onClick={() => navigate("/register")}
                                                className="flex items-center gap-2 rounded-full border border-gray-300 bg-white px-5 py-2.5 text-sm font-medium text-gray-900 transition hover:bg-gray-50"
                                            >
                                                Sign up for unlimited chats
                                                <ArrowRight className="h-4 w-4" />
                                            </button>
                                        </div>
                                    )}
                            </div>
                        ))}

                        {loading && <TypingIndicator />}
                    </>
                )}

                {exhausted && messages.length > 0 && (
                    <div className="rounded-2xl border border-amber-200 bg-amber-50 p-5 text-center">
                        <div className="mx-auto mb-3 flex h-10 w-10 items-center justify-center rounded-full bg-amber-100">
                            <Lock className="h-5 w-5 text-amber-600" />
                        </div>

                        <p className="text-sm font-semibold text-gray-900">
                            You've reached today's free guest limit
                        </p>

                        <p className="mt-1 text-sm text-gray-600">
                            Create a free account to keep chatting — history,
                            higher limits and answers from your own portfolio.
                        </p>

                        <Button
                            onClick={() => navigate("/register")}
                            className="mt-4 gap-2"
                        >
                            Create a free account
                            <ArrowRight className="h-4 w-4" />
                        </Button>
                    </div>
                )}
                </div>
            </main>

            {!exhausted && (
                <div className="shrink-0 border-t border-gray-100 bg-white px-4 py-4">
                    <div className="mx-auto w-full max-w-3xl">
                        <div className="flex items-end gap-2 rounded-3xl border border-gray-200 bg-white px-4 py-3 shadow-sm transition focus-within:border-gray-300 focus-within:shadow-md">
                            <textarea
                                value={input}
                                onChange={(event) => setInput(event.target.value)}
                                onKeyDown={(event) => {
                                    if (event.key === "Enter" && !event.shiftKey) {
                                        event.preventDefault();
                                        handleSend(input);
                                    }
                                }}
                                placeholder="Ask about the sample portfolio..."
                                rows={1}
                                className="max-h-32 flex-1 resize-none bg-transparent text-base text-gray-900 outline-none placeholder:text-lg placeholder:text-gray-400 placeholder:text-center"
                            />

                            <button
                                type="button"
                                onClick={() => handleSend(input)}
                                disabled={!input.trim() || loading}
                                className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-gray-900 text-white transition hover:bg-black disabled:cursor-not-allowed disabled:opacity-40"
                            >
                                <ArrowUp className="h-4 w-4" />
                            </button>
                        </div>

                        <p
                            className={clsx(
                                "mt-2 text-center text-xs font-medium",
                                exhausted
                                    ? "text-red-500"
                                    : quota.remaining <= 1
                                    ? "text-amber-600"
                                    : "text-gray-400"
                            )}
                        >
                            {exhausted
                                ? "Limit reached for today"
                                : `${quota.remaining} / ${quota.limit} questions remaining`}
                        </p>
                    </div>
                </div>
            )}
        </div>
    );
}
