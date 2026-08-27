package com.rag_system.template.document;

import com.rag_system.template.data.WithdrawalData;
import com.rag_system.template.result.DocumentResult;
import com.rag_system.util.MetadataKeys;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class WithdrawalDocumentTemplate implements DocumentTemplate<WithdrawalData> {

    @Override
    public DocumentResult build(WithdrawalData data) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "A withdrawal of %s was made from portfolio \"%s\".",
                data.amount(), data.portfolioName()));
        sb.append(String.format(
                "\nThe withdrawal occurred on %s.",
                data.withdrawnAt()));

        if (data.note() != null && !data.note().isBlank()) {
            sb.append(String.format(
                    "\nUser note: %s.", data.note()));
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put(MetadataKeys.PORTFOLIO_ID, data.portfolioId());
        metadata.put(MetadataKeys.PORTFOLIO_NAME, data.portfolioName());
        metadata.put(MetadataKeys.USER_ID, data.userId());
        metadata.put(MetadataKeys.TYPE, "Withdrawal");

        return new DocumentResult(sb.toString(), metadata);
    }
}
