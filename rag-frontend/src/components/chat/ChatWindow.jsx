import ChatMessage from "./ChatMessage";
import EmptyState from "./EmptyState";
import ScrollToBottom from "./ScrollToBottom";

export default function ChatWindow({
    messages,
    loading,
    onSuggest,
    onRegenerate,
    onEdit,
    hideDeveloperPanel,
}) {

    return (
        <ScrollToBottom messages={messages} loading={loading}>
            <div className="mx-auto w-full max-w-3xl px-4 py-6">
                {messages.length === 0 && !loading ? (
                    <EmptyState onSuggest={onSuggest} />
                ) : (
                    <div className="space-y-6">
                        {messages.map((message, index) => (
                            <ChatMessage
                                key={message.id}
                                message={message}
                                isLast={index === messages.length - 1}
                                onRegenerate={onRegenerate}
                                onEdit={onEdit}
                                hideDeveloperPanel={hideDeveloperPanel}
                            />
                        ))}
                    </div>
                )}
            </div>
        </ScrollToBottom>
    );
}
