package com.rag_system.template.result;

import java.util.Map;

public record DocumentResult(String content, Map<String, Object> metadata) {}
