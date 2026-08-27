import { lazy, Suspense, useEffect, useState } from "react";
import {
    ArrowUp,
    Bot,
    Check,
    Copy,
    Pencil,
    RefreshCw,
    X,
} from "lucide-react";
import { motion } from "framer-motion";
import clsx from "clsx";
import useTypewriter from "../../hooks/useTypewriter";
import SourcesPanel from "./SourcesPanel";
import DeveloperPanel from "./DeveloperPanel";

const Markdown = lazy(() => import("../markdown/Markdown"));

function formatTime(timestamp) {

    if (!timestamp) return "";

    const date = new Date(timestamp);

    return date.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
    });
}

function AssistantMessage({ message, isLast, onRegenerate, hideDeveloperPanel, maxSources }) {

    const streamed = message.streamed === true;
    const streaming = message.streaming === true;

    const { displayed, done: typewriterDone } = useTypewriter(streamed ? "" : message.content);

    const done = streamed || typewriterDone;

    const [copied, setCopied] = useState(false);

    const handleCopy = async () => {
        try {
            await navigator.clipboard.writeText(message.content);
            setCopied(true);
            setTimeout(() => setCopied(false), 2000);
        } catch {}
    };

    const renderBody = () => {
        if (streaming) {
            return (
                <p className="whitespace-pre-wrap text-[15px] leading-relaxed text-gray-800">
                    {message.content}
                    <span className="ml-0.5 inline-block h-4 w-0.5 animate-pulse bg-gray-900 align-middle" />
                </p>
            );
        }

        if (done) {
            return (
                <div className="text-[15px] leading-relaxed text-gray-800">
                    <Suspense
                        fallback={
                            <p className="whitespace-pre-wrap">{message.content}</p>
                        }
                    >
                        <Markdown content={message.content} />
                    </Suspense>
                </div>
            );
        }

        return (
            <p className="whitespace-pre-wrap text-[15px] leading-relaxed text-gray-800">
                {displayed}
                <span className="ml-0.5 inline-block h-4 w-0.5 animate-pulse bg-gray-900 align-middle" />
            </p>
        );
    };

    return (
        <div className="group flex w-full gap-4">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-gray-900 text-white">
                <Bot className="h-4 w-4" />
            </div>

            <div className="min-w-0 flex-1">
                <div className="mb-1 flex items-center gap-2">
                    <span className="text-sm font-medium text-gray-900">
                        RAG Assistant
                    </span>
                    <span className="text-[11px] text-gray-400">
                        {formatTime(message.createdAt)}
                    </span>

                    {done && (
                        <span className="ml-auto flex items-center gap-1 opacity-0 transition group-hover:opacity-100">
                            {isLast && onRegenerate && (
                                <button
                                    type="button"
                                    onClick={() => onRegenerate(message)}
                                    className="flex items-center gap-1 rounded-md px-1.5 py-1 text-[11px] font-medium text-gray-400 transition hover:bg-gray-100 hover:text-gray-600"
                                >
                                    <RefreshCw className="h-3 w-3" />
                                    Regenerate
                                </button>
                            )}
                        </span>
                    )}
                </div>

                {renderBody()}

                {done && (
                    <div className="mt-1.5 flex items-center gap-1">
                        <button
                            type="button"
                            onClick={handleCopy}
                            aria-label={copied ? "Copied" : "Copy response"}
                            title={copied ? "Copied" : "Copy response"}
                            className="flex h-7 w-7 items-center justify-center rounded-md text-gray-400 opacity-0 transition hover:bg-gray-100 hover:text-gray-600 group-hover:opacity-100"
                        >
                            {copied ? (
                                <Check className="h-3.5 w-3.5 text-emerald-500" />
                            ) : (
                                <Copy className="h-3.5 w-3.5" />
                            )}
                        </button>
                    </div>
                )}

                {done && message.sources?.length > 0 && (
                    <SourcesPanel
                        sources={message.sources}
                        maxVisible={maxSources}
                        averageSimilarity={message.averageSimilarity}
                        highestSimilarity={message.highestSimilarity}
                        contextDocuments={message.contextDocuments}
                    />
                )}

                {!hideDeveloperPanel && <DeveloperPanel message={message} />}
            </div>
        </div>
    );
}

function UserMessage({ message, isLast, onEdit }) {

    const [editing, setEditing] = useState(false);
    const [editValue, setEditValue] = useState(message.content);

    useEffect(() => {
        setEditValue(message.content);
    }, [message.content]);

    const handleSave = () => {
        const text = editValue.trim();
        if (!text) return;
        setEditing(false);
        onEdit?.(text);
    };

    if (editing) {
        return (
            <div className="flex w-full justify-end">
                <div className="max-w-[80%]">
                    <textarea
                        autoFocus
                        value={editValue}
                        onChange={(event) => setEditValue(event.target.value)}
                        onKeyDown={(event) => {
                            if (event.key === "Enter" && !event.shiftKey) {
                                event.preventDefault();
                                handleSave();
                            }
                            if (event.key === "Escape") {
                                setEditing(false);
                                setEditValue(message.content);
                            }
                        }}
                        rows={Math.min(Math.max(editValue.split("\n").length, 2), 8)}
                        className="w-full resize-none rounded-3xl bg-gray-100 px-4 py-2.5 text-[15px] leading-relaxed text-gray-900 outline-none"
                    />

                    <div className="mt-1.5 flex items-center justify-end gap-1.5">
                        <button
                            type="button"
                            onClick={() => {
                                setEditing(false);
                                setEditValue(message.content);
                            }}
                            aria-label="Cancel edit"
                            className="flex h-8 w-8 items-center justify-center rounded-full text-gray-500 transition hover:bg-gray-100 hover:text-gray-900"
                        >
                            <X className="h-4 w-4" />
                        </button>

                        <button
                            type="button"
                            onClick={handleSave}
                            disabled={!editValue.trim()}
                            aria-label="Save edit"
                            className="flex h-8 w-8 items-center justify-center rounded-full bg-gray-900 text-white transition hover:bg-black disabled:cursor-not-allowed disabled:opacity-40"
                        >
                            <ArrowUp className="h-4 w-4" />
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="group flex w-full justify-end">
            <div className="max-w-[80%]">
                <div
                    className={clsx(
                        "whitespace-pre-wrap rounded-3xl px-4 py-2.5 text-[15px] leading-relaxed text-gray-900",
                        "bg-gray-100"
                    )}
                >
                    {message.content}
                </div>

                <div className="mt-1 flex items-center justify-end gap-1">
                    <span className="text-[11px] text-gray-400">
                        {formatTime(message.createdAt)}
                    </span>

                    {isLast && onEdit && (
                        <button
                            type="button"
                            onClick={() => setEditing(true)}
                            className="flex items-center gap-1 rounded-md px-1.5 py-1 text-[11px] font-medium text-gray-400 opacity-0 transition hover:bg-gray-100 hover:text-gray-600 group-hover:opacity-100"
                        >
                            <Pencil className="h-3 w-3" />
                            Edit
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

export default function ChatMessage({ message, isLast, onRegenerate, onEdit, hideDeveloperPanel, maxSources }) {

    return (
        <motion.div
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.25, ease: "easeOut" }}
        >
            {message.role === "user" ? (
                <UserMessage
                    message={message}
                    isLast={isLast}
                    onEdit={onEdit}
                />
            ) : (
                <AssistantMessage
                    message={message}
                    isLast={isLast}
                    onRegenerate={onRegenerate}
                    hideDeveloperPanel={hideDeveloperPanel}
                    maxSources={maxSources}
                />
            )}
        </motion.div>
    );
}
