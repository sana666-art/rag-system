import { useCallback, useEffect, useRef, useState } from "react";
import toast from "react-hot-toast";

import { streamRag } from "../services/ragService";
import {
    listSessions,
    getMessages,
    renameSession,
    deleteSession,
    deleteMessage,
} from "../api/chatApi";

import {
    createUserMessage,
    createAssistantMessage,
    fromApiMessage,
} from "../models/message";

const isServerId = (id) => typeof id === "number";

function ragFromStream(m) {
    return {
        answer: m.answer,
        model: m.model,
        sources: m.sources,
        contextDocuments: m.contextDocuments,
        averageSimilarity: m.averageSimilarity,
        highestSimilarity: m.highestSimilarity,
        promptCharacters: m.promptCharacters,
        estimatedPromptTokens: m.estimatedPromptTokens,
        promptTokens: m.promptTokens,
        completionTokens: m.completionTokens,
        totalTokens: m.totalTokens,
        embeddingTime: m.embeddingTime,
        retrievalTime: m.retrievalTime,
        llmTime: m.llmTime,
        totalTime: m.totalTime,
    };
}

function sourcesStats(sources = []) {
    if (!sources.length) return {};
    const similarities = sources
        .map((s) => s.similarity)
        .filter((value) => typeof value === "number");
    if (!similarities.length) return {};
    return {
        averageSimilarity:
            similarities.reduce((a, b) => a + b, 0) / similarities.length,
        highestSimilarity: Math.max(...similarities),
    };
}

export default function useChat() {

    const [sessions, setSessions] = useState([]);
    const [messages, setMessages] = useState([]);
    const [sessionId, setSessionId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [sessionsLoading, setSessionsLoading] = useState(false);
    const abortRef = useRef(null);
    const sessionIdRef = useRef(null);
    const sendingRef = useRef(false);

    const applySessionId = (value) => {
        sessionIdRef.current = value;
        setSessionId(value);
    };

    const patchMessage = (id, patchOrFn) => {
        setMessages((previous) =>
            previous.flatMap((message) => {
                if (message.id !== id) return [message];
                const next =
                    typeof patchOrFn === "function"
                        ? patchOrFn(message)
                        : { ...message, ...patchOrFn };
                return next ? [next] : [];
            })
        );
    };

    const refreshSessions = useCallback(async () => {
        try {
            setSessionsLoading(true);
            const data = await listSessions();
            setSessions(data);
        } catch {
            // keep existing sessions on failure
        } finally {
            setSessionsLoading(false);
        }
    }, []);

    useEffect(() => {
        refreshSessions();
    }, [refreshSessions]);

    async function sendMessage(question) {

        if (!question.trim() || loading || sendingRef.current) return;

        sendingRef.current = true;

        abortRef.current?.abort();

        const controller = new AbortController();

        abortRef.current = controller;

        const userMessage = createUserMessage(question, { sessionId: sessionIdRef.current });

        const tempAssistantId = crypto.randomUUID();

        const assistantPlaceholder = {
            ...createAssistantMessage({ answer: "" }, { id: tempAssistantId }),
            streaming: true,
        };

        setMessages(previous => [
            ...previous,
            userMessage,
            assistantPlaceholder,
        ]);
        setLoading(true);

        try {

            await streamRag({
                question,
                sessionId: sessionIdRef.current,
                signal: controller.signal,
                onSession: (data) => {
                    if (data.sessionId) {
                        applySessionId(data.sessionId);
                    }
                },
                onRetrieval: (data) => {
                    patchMessage(tempAssistantId, {
                        sources: data.sources,
                        contextDocuments: data.contextDocuments,
                        ...sourcesStats(data.sources),
                    });
                },
                onToken: (data) => {
                    patchMessage(tempAssistantId, {
                        content: (content) => (content ?? "") + data.token,
                    });
                },
                onDone: (done) => {
                    const resolvedSessionId = done.sessionId ?? sessionIdRef.current;

                    applySessionId(resolvedSessionId);

                    patchMessage(userMessage.id, {
                        id: done.userMessageId ?? userMessage.id,
                        sessionId: resolvedSessionId,
                    });

                    const assistantMessage = createAssistantMessage(
                        ragFromStream(done.rag),
                        {
                            id: done.assistantMessageId ?? tempAssistantId,
                            sessionId: resolvedSessionId,
                        }
                    );

                    patchMessage(tempAssistantId, {
                        ...assistantMessage,
                        streaming: false,
                        streamed: true,
                    });

                    refreshSessions();
                },
            });

        } catch (error) {

            if (error?.name === "AbortError" || controller.signal.aborted) return;

            patchMessage(tempAssistantId, (message) => {
                const content = (message.content ?? "").trim();
                return content
                    ? { ...message, streaming: false, streamed: true }
                    : null;
            });

            toast.error(
                error?.message || "Something went wrong. Please try again."
            );

        } finally {
            setLoading(false);
            sendingRef.current = false;
            if (abortRef.current === controller) {
                abortRef.current = null;
            }
        }
    }

    function stopGenerating() {
        abortRef.current?.abort();
    }

    async function regenerate() {

        if (loading || !sessionIdRef.current) return;

        const lastUser = [...messages].reverse().find(m => m.role === "user");
        const lastMessage = messages[messages.length - 1];

        if (!lastUser || !lastMessage || lastMessage.role !== "assistant") return;

        const controller = new AbortController();

        abortRef.current?.abort();
        abortRef.current = controller;

        const tempAssistantId = lastMessage.id ?? crypto.randomUUID();

        setLoading(true);

        try {

            if (isServerId(lastMessage.id)) {
                await deleteMessage(sessionIdRef.current, lastMessage.id);
            }

            setMessages(previous => {
                const index = previous.findIndex(m => m.id === lastMessage.id);
                const head = index >= 0 ? previous.slice(0, index) : previous;
                return [
                    ...head,
                    {
                        ...createAssistantMessage({ answer: "" }, { id: tempAssistantId }),
                        streaming: true,
                    },
                ];
            });

            await streamRag({
                question: lastUser.content,
                sessionId: sessionIdRef.current,
                signal: controller.signal,
                onSession: (data) => {
                    if (data.sessionId) {
                        applySessionId(data.sessionId);
                    }
                },
                onRetrieval: (data) => {
                    patchMessage(tempAssistantId, {
                        sources: data.sources,
                        contextDocuments: data.contextDocuments,
                        ...sourcesStats(data.sources),
                    });
                },
                onToken: (data) => {
                    patchMessage(tempAssistantId, {
                        content: (content) => (content ?? "") + data.token,
                    });
                },
                onDone: async (done) => {
                    const resolvedSessionId = done.sessionId ?? sessionIdRef.current;

                    applySessionId(resolvedSessionId);

                    if (
                        done.userMessageId &&
                        !isServerId(lastUser.id)
                    ) {
                        patchMessage(lastUser.id, {
                            id: done.userMessageId,
                            sessionId: resolvedSessionId,
                        });
                    } else if (done.userMessageId) {
                        try {
                            await deleteMessage(resolvedSessionId, done.userMessageId);
                        } catch {
                            // duplicate already gone
                        }
                    }

                    const assistantMessage = createAssistantMessage(
                        ragFromStream(done.rag),
                        {
                            id: done.assistantMessageId ?? tempAssistantId,
                            sessionId: resolvedSessionId,
                        }
                    );

                    patchMessage(tempAssistantId, {
                        ...assistantMessage,
                        streaming: false,
                        streamed: true,
                    });

                    refreshSessions();
                },
            });

        } catch (error) {

            if (error?.name === "AbortError" || controller.signal.aborted) return;

            patchMessage(tempAssistantId, (message) => {
                const content = (message.content ?? "").trim();
                return content
                    ? { ...message, streaming: false, streamed: true }
                    : null;
            });

            toast.error(
                error?.message || "Something went wrong. Please try again."
            );

        } finally {
            setLoading(false);
            if (abortRef.current === controller) {
                abortRef.current = null;
            }
        }
    }

    async function editMessage(question) {

        const text = question.trim();

        if (!text || loading || !sessionIdRef.current) return;

        const lastUser = [...messages].reverse().find(m => m.role === "user");

        if (!lastUser) return;

        const controller = new AbortController();

        abortRef.current?.abort();
        abortRef.current = controller;

        const tempUserId = crypto.randomUUID();
        const tempAssistantId = crypto.randomUUID();

        setLoading(true);

        try {

            if (isServerId(lastUser.id)) {
                await deleteMessage(sessionIdRef.current, lastUser.id);
            }

            const lastMessage = messages[messages.length - 1];

            if (
                lastMessage &&
                lastMessage.role === "assistant" &&
                isServerId(lastMessage.id)
            ) {
                await deleteMessage(sessionIdRef.current, lastMessage.id);
            }

            setMessages(previous => {
                const index = previous.findIndex(m => m.id === lastUser.id);
                const head = index >= 0 ? previous.slice(0, index) : [];
                return [
                    ...head,
                    createUserMessage(text, {
                        id: tempUserId,
                        sessionId: sessionIdRef.current,
                    }),
                    {
                        ...createAssistantMessage({ answer: "" }, { id: tempAssistantId }),
                        streaming: true,
                    },
                ];
            });

            await streamRag({
                question: text,
                sessionId: sessionIdRef.current,
                signal: controller.signal,
                onSession: (data) => {
                    if (data.sessionId) {
                        applySessionId(data.sessionId);
                    }
                },
                onRetrieval: (data) => {
                    patchMessage(tempAssistantId, {
                        sources: data.sources,
                        contextDocuments: data.contextDocuments,
                        ...sourcesStats(data.sources),
                    });
                },
                onToken: (data) => {
                    patchMessage(tempAssistantId, {
                        content: (content) => (content ?? "") + data.token,
                    });
                },
                onDone: (done) => {
                    const resolvedSessionId = done.sessionId ?? sessionIdRef.current;

                    applySessionId(resolvedSessionId);

                    patchMessage(tempUserId, {
                        id: done.userMessageId ?? tempUserId,
                        sessionId: resolvedSessionId,
                    });

                    const assistantMessage = createAssistantMessage(
                        ragFromStream(done.rag),
                        {
                            id: done.assistantMessageId ?? tempAssistantId,
                            sessionId: resolvedSessionId,
                        }
                    );

                    patchMessage(tempAssistantId, {
                        ...assistantMessage,
                        streaming: false,
                        streamed: true,
                    });

                    refreshSessions();
                },
            });

        } catch (error) {

            if (error?.name === "AbortError" || controller.signal.aborted) return;

            patchMessage(tempAssistantId, (message) => {
                const content = (message.content ?? "").trim();
                return content
                    ? { ...message, streaming: false, streamed: true }
                    : null;
            });

            toast.error(
                error?.message || "Something went wrong. Please try again."
            );

        } finally {
            setLoading(false);
            if (abortRef.current === controller) {
                abortRef.current = null;
            }
        }
    }

    async function loadConversation(loadedSessionId) {

        setLoading(true);
        applySessionId(loadedSessionId);

        try {
            const apiMessages = await getMessages(loadedSessionId);

            const mapped = apiMessages.map(message =>
                fromApiMessage(message, { sessionId: loadedSessionId }));

            setMessages(mapped);
        } catch {
            setMessages([]);
        } finally {
            setLoading(false);
        }
    }

    function newChat() {
        applySessionId(null);
        setMessages([]);
    }

    async function handleRenameSession(id, title) {
        await renameSession(id, title);
        await refreshSessions();
    }

    async function handleDeleteSession(id) {

        await deleteSession(id);

        setSessions(previous => previous.filter(s => s.id !== id));

        if (sessionId === id) {
            newChat();
        }
    }

    return {
        sessions,
        sessionsLoading,
        messages,
        sessionId,
        loading,
        sendMessage,
        stopGenerating,
        regenerate,
        editMessage,
        loadConversation,
        newChat,
        refreshSessions,
        renameSession: handleRenameSession,
        deleteSession: handleDeleteSession,
    };
}
