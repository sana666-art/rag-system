import api from "./axios";

export async function listSessions() {
    const response = await api.get("/api/chat/sessions");
    return response.data;
}

export async function getMessages(sessionId) {
    const response = await api.get(`/api/chat/sessions/${sessionId}/messages`);
    return response.data;
}

export async function renameSession(sessionId, title) {
    const response = await api.patch(`/api/chat/sessions/${sessionId}`, { title });
    return response.data;
}

export async function deleteSession(sessionId) {
    const response = await api.delete(`/api/chat/sessions/${sessionId}`);
    return response.data;
}

export async function deleteMessage(sessionId, messageId) {
    const response = await api.delete(`/api/chat/sessions/${sessionId}/messages/${messageId}`);
    return response.data;
}

export async function exportSession(sessionId) {
    const response = await api.get(`/api/chat/sessions/${sessionId}/export`, {
        responseType: "blob",
    });
    return response.data;
}
