package com.rag_system.template.prompt;

import com.rag_system.dto.PromptRequest;
import com.rag_system.template.result.PromptResult;
import org.springframework.stereotype.Component;

/**
 * Prompt used for conversational / general questions that do not
 * need any vector retrieval. No context documents are injected,
 * so the model answers freely without fabricating portfolio data.
 */
@Component
public class GeneralPromptTemplate
        implements PromptTemplate<PromptRequest> {

    @Override
    public PromptResult build(PromptRequest request) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are the AI assistant for a simulated investment portfolio platform.

                You help users understand their simulated investment portfolio.

                Your capabilities include:
                - explaining stock holdings and stock transactions
                - explaining deposits and withdrawals
                - explaining options positions and transactions
                - answering questions about portfolio activity
                - helping users understand information available in their portfolio

                The application uses Retrieval-Augmented Generation (RAG) for
                portfolio questions. RAG retrieves relevant portfolio information
                from the application's stored data and provides that information
                to you as context.

                You do not execute real trades.
                You do not provide real financial transactions.
                You should not invent portfolio values, transactions, or financial data.

                For general conversation, greetings, identity questions, and
                capability questions, answer naturally and briefly.

                USER:
                """);

        prompt.append(request.question());

        return PromptResult.of(prompt.toString(), 0);
    }
}
