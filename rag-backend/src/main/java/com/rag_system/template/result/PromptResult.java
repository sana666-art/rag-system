package com.rag_system.template.result;

public record PromptResult(
        String prompt,
        int contextDocuments,
        int promptCharacters,
        int estimatedPromptTokens
) {

    private static final int CHARS_PER_TOKEN = 4;

    public static PromptResult of(
            String prompt, int contextDocuments) {

        int chars = prompt.length();

        return new PromptResult(
                prompt,
                contextDocuments,
                chars,
                chars / CHARS_PER_TOKEN);
    }
}
