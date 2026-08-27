package com.rag_system.template.document;

import com.rag_system.template.data.DepositData;
import com.rag_system.template.result.DocumentResult;
import com.rag_system.util.MetadataKeys;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DepositDocumentTemplate implements DocumentTemplate<DepositData> {

    @Override
    public DocumentResult build(DepositData data) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "A deposit of %s was made into portfolio \"%s\".",
                data.amount(), data.portfolioName()));
        sb.append(String.format(
                "\nThe deposit occurred on %s.",
                data.depositedAt()));

        if (data.note() != null && !data.note().isBlank()) {
            sb.append(String.format(
                    "\nUser note: %s.", data.note()));
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put(MetadataKeys.PORTFOLIO_ID, data.portfolioId());
        metadata.put(MetadataKeys.PORTFOLIO_NAME, data.portfolioName());
        metadata.put(MetadataKeys.USER_ID, data.userId());
        metadata.put(MetadataKeys.TYPE, "Deposit");

        return new DocumentResult(sb.toString(), metadata);
    }
}
