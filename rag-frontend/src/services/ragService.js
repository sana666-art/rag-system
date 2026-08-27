import { askQuestion, streamQuestion } from "../api/ragApi";

export async function askRag({
    question,
    sessionId = null,
    portfolioId = null,
    source = null,
    metadataFilters = [],
    signal = null,
}) {

    return await askQuestion({

        question,

        sessionId,

        portfolioId,

        source,

        metadataFilters,

    }, signal);

}

export function streamRag({
    question,
    sessionId = null,
    portfolioId = null,
    source = null,
    metadataFilters = [],
    signal = null,
    onSession,
    onRetrieval,
    onToken,
    onDone,
}) {

    return streamQuestion({

        question,

        sessionId,

        portfolioId,

        source,

        metadataFilters,

    }, {
        signal,
        onEvent: (eventName, data) => {
            if (eventName === "session") onSession?.(data);
            if (eventName === "retrieval") onRetrieval?.(data);
            if (eventName === "token") onToken?.(data);
        },
    }).then((done) => {
        onDone?.(done);
    });
}
