export function createUserMessage(content, { id = null, sessionId = null } = {}) {

    return {

        id: id ?? crypto.randomUUID(),

        sessionId,

        role: "user",

        content,

        createdAt: Date.now(),

    };

}

export function createAssistantMessage(rag, { id = null, sessionId = null } = {}) {

    return {

        id: id ?? crypto.randomUUID(),

        sessionId,

        role: "assistant",

        content: rag.answer,

        model: rag.model,

        sources: rag.sources,

        contextDocuments: rag.contextDocuments,

        averageSimilarity: rag.averageSimilarity,

        highestSimilarity: rag.highestSimilarity,

        promptCharacters: rag.promptCharacters,

        estimatedPromptTokens: rag.estimatedPromptTokens,

        promptTokens: rag.promptTokens,

        completionTokens: rag.completionTokens,

        totalTokens: rag.totalTokens,

        timings: {

            embedding: rag.embeddingTime,

            retrieval: rag.retrievalTime,

            llm: rag.llmTime,

            total: rag.totalTime,

        },

        createdAt: Date.now(),

    };

}

export function fromApiMessage(apiMessage, { sessionId = null } = {}) {

    const widget = apiMessage.uiWidget || {};

    const tokens = widget.tokens || {};

    const timings = widget.timings || {};

    const retrieval = widget.retrieval || {};

    return {

        id: apiMessage.id ?? crypto.randomUUID(),

        sessionId,

        role: apiMessage.role === "USER" ? "user" : "assistant",

        content: apiMessage.content,

        model: widget.model,

        sources: widget.sources,

        contextDocuments: undefined,

        averageSimilarity: retrieval.averageSimilarity,

        highestSimilarity: retrieval.highestSimilarity,

        promptTokens: tokens.prompt,

        completionTokens: tokens.completion,

        totalTokens: tokens.total,

        timings: {

            embedding: timings.embedding,

            retrieval: timings.retrieval,

            llm: timings.llm,

            total: timings.total,

        },

        createdAt: apiMessage.timestamp

            ? new Date(apiMessage.timestamp).getTime()

            : Date.now(),

    };

}
