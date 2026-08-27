package com.rag_system.template.document;

import com.rag_system.template.data.OptionsTransactionData;
import com.rag_system.template.result.DocumentResult;
import com.rag_system.util.MetadataKeys;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OptionsTransactionDocumentTemplate implements DocumentTemplate<OptionsTransactionData> {

    @Override
    public DocumentResult build(OptionsTransactionData data) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "An options %s transaction was executed in portfolio \"%s\".",
                data.action(), data.portfolioName()));
        sb.append(String.format(
                "\nThe trade involved %s contracts of %s.",
                data.quantity(), data.optionTicker()));
        sb.append(String.format(
                "\nEach contract cost %s.", data.pricePerContract()));
        sb.append(String.format(
                "\nThe total cost was %s.", data.totalCost()));
        sb.append(String.format(
                "\nThe fee was %s.", data.fee()));

        if (data.realizedPnl() != null) {
            sb.append(String.format(
                    "\nRealised profit/loss: %s.", data.realizedPnl()));
        }

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
        metadata.put(MetadataKeys.TYPE, "OptionsTransaction");
        metadata.put(MetadataKeys.OPTION_TICKER, data.optionTicker());
        metadata.put(MetadataKeys.ACTION, data.action());

        return new DocumentResult(sb.toString(), metadata);
    }
}
