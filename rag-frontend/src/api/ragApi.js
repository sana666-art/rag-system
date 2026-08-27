import api from "./axios";
import { getAccessToken } from "../utils/storage";

export async function askQuestion(payload, signal) {
    const response = await api.post("/api/chat/ask", payload, { signal });
    return response.data;
}

export function streamQuestion(payload, { signal, onEvent }) {

    return new Promise((resolve, reject) => {

        const base = import.meta.env.VITE_API_BASE_URL;

        const controller = new AbortController();

        const forwardAbort = () => controller.abort();

        signal?.addEventListener("abort", forwardAbort, { once: true });

        const headers = { "Content-Type": "application/json" };

        const token = getAccessToken();

        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }

        fetch(`${base}/api/chat/stream`, {
            method: "POST",
            headers,
            body: JSON.stringify(payload),
            signal: controller.signal,
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(
                        response.status === 401
                            ? "Session expired. Please log in again."
                            : `Request failed with status ${response.status}`
                    );
                }
                return response;
            })
            .then((response) => {
                if (!response.body) {
                    throw new Error("Streaming not supported by this browser");
                }

                const reader = response.body.getReader();
                const decoder = new TextDecoder("utf-8");

                let buffer = "";
                let settled = false;

                const handleFrame = (frame) => {
                    let eventName = "message";
                    const dataLines = [];

                    for (const line of frame.split("\n")) {
                        if (line.startsWith("event:")) {
                            eventName = line.slice(6).trim();
                        } else if (line.startsWith("data:")) {
                            dataLines.push(line.slice(5).trimStart());
                        }
                    }

                    if (dataLines.length === 0) return;

                    let data;
                    try {
                        data = JSON.parse(dataLines.join("\n"));
                    } catch {
                        data = dataLines.join("\n");
                    }

                    if (eventName === "done") {
                        settled = true;
                        resolve(data);
                        return;
                    }

                    if (eventName === "error") {
                        settled = true;
                        reject(new Error(data?.message || "Streaming error"));
                        return;
                    }

                    onEvent?.(eventName, data);
                };

                const pump = () => {
                    reader.read().then(({ done, value }) => {
                        if (done) {
                            signal?.removeEventListener("abort", forwardAbort);
                            if (!settled) {
                                if (signal?.aborted) {
                                    const error = new Error("Request aborted");
                                    error.name = "AbortError";
                                    reject(error);
                                } else {
                                    reject(new Error("Stream ended unexpectedly"));
                                }
                            }
                            return;
                        }

                        buffer += decoder.decode(value, { stream: true });

                        let boundary;
                        while ((boundary = buffer.indexOf("\n\n")) >= 0) {
                            const frame = buffer.slice(0, boundary);
                            buffer = buffer.slice(boundary + 2);
                            handleFrame(frame);
                        }

                        pump();
                    }).catch((error) => {
                        signal?.removeEventListener("abort", forwardAbort);
                        if (error?.name === "AbortError" || signal?.aborted) {
                            const abortError = new Error("Request aborted");
                            abortError.name = "AbortError";
                            reject(abortError);
                        } else {
                            reject(error);
                        }
                    });
                };

                pump();
            })
            .catch((error) => {
                signal?.removeEventListener("abort", forwardAbort);
                if (error?.name === "AbortError" || signal?.aborted) {
                    const abortError = new Error("Request aborted");
                    abortError.name = "AbortError";
                    reject(abortError);
                } else {
                    reject(error);
                }
            });
    });
}
