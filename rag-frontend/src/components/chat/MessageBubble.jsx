import clsx from "clsx";

export default function MessageBubble({ message }) {

    const isUser = message.role === "user";

    return (
        <div
            className={clsx(
                "flex",
                isUser ? "justify-end" : "justify-start"
            )}
        >
            <div
                className={clsx(
                    "max-w-[75%] whitespace-pre-wrap rounded-2xl px-4 py-2.5 text-sm leading-relaxed",
                    isUser
                        ? "bg-indigo-600 text-white"
                        : "border border-gray-200 bg-white text-gray-800"
                )}
            >
                {message.content}
            </div>
        </div>
    );
}
