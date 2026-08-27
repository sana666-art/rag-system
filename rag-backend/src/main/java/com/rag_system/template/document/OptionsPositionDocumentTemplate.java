package com.rag_system.template.document;

import com.rag_system.template.data.OptionsPositionData;
import com.rag_system.template.result.DocumentResult;
import com.rag_system.util.MetadataKeys;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OptionsPositionDocumentTemplate implements DocumentTemplate<OptionsPositionData> {

    @Override
    public DocumentResult build(OptionsPositionData data) {

        String positionState = switch (data.status()) {
            case "OPEN" -> "currently holds an open";
            case "CLOSED" -> "has a closed";
            case "EXERCISED" -> "has an exercised";
            default -> "holds a " + data.status().toLowerCase() + " position for a";
        };

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "The portfolio \"%s\" %s %s option.",
                data.portfolioName(), positionState, data.contractType()));
        sb.append(String.format(
                "\nUnderlying stock: %s.", data.underlyingTicker()));
        sb.append(String.format(
                "\nStrike price: %s.", data.strikePrice()));
        sb.append(String.format(
                "\nExpiration: %s.", data.expirationDate()));
        sb.append(String.format(
                "\nContracts: %d.", data.quantity()));
        sb.append(String.format(
                "\nAverage cost per contract: %s.", data.avgCostPerContract()));
        sb.append(String.format(
                "\nSide: %s.", data.side()));
        sb.append(String.format(
                "\nCurrent status: %s.", data.status()));

        if (data.realizedPnl() != null) {
            sb.append(String.format(
                    "\nRealised profit/loss: %s.", data.realizedPnl()));
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put(MetadataKeys.PORTFOLIO_ID, data.portfolioId());
        metadata.put(MetadataKeys.PORTFOLIO_NAME, data.portfolioName());
        metadata.put(MetadataKeys.USER_ID, data.userId());
        metadata.put(MetadataKeys.TYPE, "OptionsPosition");
        metadata.put(MetadataKeys.OPTION_TICKER, data.optionTicker());
        metadata.put(MetadataKeys.UNDERLYING_TICKER, data.underlyingTicker());
        metadata.put(MetadataKeys.CONTRACT_TYPE, data.contractType());
        metadata.put(MetadataKeys.STATUS, data.status());

        return new DocumentResult(sb.toString(), metadata);
    }
}
