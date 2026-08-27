import MessageBubble from "./MessageBubble";
import EmptyState from "./EmptyState";
import TypingIndicator from "./TypingIndicator";
import ScrollToBottom from "./ScrollToBottom";

export default function MessageList({ messages, loading }) {

    return (
        <ScrollToBottom messages={messages} loading={loading}>
            {messages.length === 0 && !loading ? (
                <EmptyState />
            ) : (
                <div className="space-y-4 px-5 py-5">
                    {messages.map((message) => (
                        <MessageBubble key={message.id} message={message} />
                    ))}

                    {loading && <TypingIndicator />}
                </div>
            )}
        </ScrollToBottom>
    );
}
