package com.rag_system.controller;

import com.rag_system.dto.generateDocumentDTO.GenerateDocumentsResponse;
import com.rag_system.exception.AppException;
import com.rag_system.service.impl.DocumentGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentGenerationService documentGenerationService;

    @PostMapping("/generate/{type}")
    public ResponseEntity<GenerateDocumentsResponse> generateDocuments(
            @PathVariable String type) {

        GenerateDocumentsResponse response = switch (type.toLowerCase()) {
            case "portfolio" ->
                    documentGenerationService.generatePortfolioDocuments();
            case "deposit" ->
                    documentGenerationService.generateDepositDocuments();
            case "withdrawal" ->
                    documentGenerationService.generateWithdrawalDocuments();
            case "stock-transactions" ->
                    documentGenerationService.generateStockTransactionDocuments();
            case "options-transactions" ->
                    documentGenerationService.generateOptionsTransactionDocuments();
            case "options-positions" ->
                    documentGenerationService.generateOptionsPositionDocuments();
            default -> throw new AppException(
                    "Unknown document type: " + type
                            + ". Valid types: portfolio, deposit, withdrawal, "
                            + "stock-transactions, options-transactions, "
                            + "options-positions",
                    HttpStatus.BAD_REQUEST);
        };

        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate/all")
    public ResponseEntity<GenerateDocumentsResponse> generateAllDocuments() {

        GenerateDocumentsResponse response =
                documentGenerationService.generateAllDocuments();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/regenerate/options-positions")
    public ResponseEntity<GenerateDocumentsResponse>
    regenerateOptionsPositionDocuments() {

        GenerateDocumentsResponse response =
                documentGenerationService
                        .regenerateOptionsPositionDocuments();

        return ResponseEntity.ok(response);
    }

}
