package com.rag_system.template.document;

import com.rag_system.template.data.StockTransactionData;
import com.rag_system.template.result.DocumentResult;
import com.rag_system.util.MetadataKeys;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class StockTransactionDocumentTemplate implements DocumentTemplate<StockTransactionData> {

    @Override
    public DocumentResult build(StockTransactionData data) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "A %s transaction was executed in portfolio \"%s\".",
                data.type(), data.portfolioName()));
        sb.append(String.format(
                "\nThe investor %s %s shares of %s.",
                data.type().equalsIgnoreCase("BUY") ? "purchased" : "sold",
                data.quantity(),
                data.symbol()));
        sb.append(String.format(
                "\nEach share cost %s.", data.price()));
        sb.append(String.format(
                "\nThe trading fee was %s.", data.fee()));
        sb.append(String.format(
                "\nTransaction source: %s.", data.source()));
        sb.append(String.format(
                "\nExecuted on %s.", data.executedAt()));

        if (data.note() != null && !data.note().isBlank()) {
            sb.append(String.format(
                    "\nUser note: %s.", data.note()));
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put(MetadataKeys.PORTFOLIO_ID, data.portfolioId());
        metadata.put(MetadataKeys.PORTFOLIO_NAME, data.portfolioName());
        metadata.put(MetadataKeys.USER_ID, data.userId());
        metadata.put(MetadataKeys.TYPE, "StockTransaction");
        metadata.put(MetadataKeys.SYMBOL, data.symbol());
        metadata.put(MetadataKeys.ACTION, data.type());

        return new DocumentResult(sb.toString(), metadata);
    }
}
