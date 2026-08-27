package com.rag_system.enums;

public enum QuestionIntent {

    /**
     * Greetings, small talk, thanks, goodbye, etc.
     */
    GENERAL,

    /**
     * Questions about the AI assistant or the application itself.
     */
    APPLICATION_INFO,

    /**
     * Questions requiring portfolio data.
     */
    PORTFOLIO,

    /**
     * Question could not be confidently classified.
     */
    UNKNOWN
}
