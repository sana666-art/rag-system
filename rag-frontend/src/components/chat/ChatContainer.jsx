import { useMemo, useState } from "react";
import useChat from "../../hooks/useChat";
import Sidebar from "../layout/Sidebar";
import ChatWindow from "./ChatWindow";
import ChatHeader from "./ChatHeader";
import ChatInput from "./ChatInput";

export default function ChatContainer() {

    const chat = useChat();
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
    const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);

    const currentSession = chat.sessions.find(
        (session) => session.id === chat.sessionId
    );

    const title = currentSession?.title || "New chat";

    const model = useMemo(() => {
        for (let i = chat.messages.length - 1; i >= 0; i -= 1) {
            const message = chat.messages[i];
            if (message.role === "assistant" && message.model) {
                return message.model;
            }
        }
        return null;
    }, [chat.messages]);

    const handleToggleSidebar = () => {
        if (window.matchMedia("(min-width: 768px)").matches) {
            setSidebarCollapsed((value) => !value);
        } else {
            setMobileSidebarOpen((value) => !value);
        }
    };

    return (
        <div className="flex w-full">
            <Sidebar
                sessions={chat.sessions}
                loading={chat.sessionsLoading}
                activeSessionId={chat.sessionId}
                collapsed={sidebarCollapsed}
                mobileOpen={mobileSidebarOpen}
                onCloseMobile={() => setMobileSidebarOpen(false)}
                onNewChat={chat.newChat}
                onSelectSession={chat.loadConversation}
                onRenameSession={chat.renameSession}
                onDeleteSession={chat.deleteSession}
            />

            <main className="flex h-screen min-w-0 flex-1 flex-col bg-white">
                <ChatHeader
                    title={title}
                    model={model}
                    onToggleSidebar={handleToggleSidebar}
                />

                <ChatWindow
                    messages={chat.messages}
                    loading={chat.loading}
                    onSuggest={chat.sendMessage}
                    onRegenerate={chat.regenerate}
                    onEdit={chat.editMessage}
                    hideDeveloperPanel
                />

                <ChatInput
                    onSend={chat.sendMessage}
                    onStop={chat.stopGenerating}
                    disabled={chat.loading}
                    loading={chat.loading}
                />
            </main>
        </div>
    );
}
