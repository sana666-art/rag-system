package com.rag_system.service.impl;

import com.rag_system.builder.*;
import com.rag_system.dto.generateDocumentDTO.GenerateDocumentsResponse;
import com.rag_system.entity.*;
import com.rag_system.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentGenerationService {

    private static final Logger log =
            LoggerFactory.getLogger(DocumentGenerationService.class);

    private final GenericDocumentGenerationService generator;

    @PersistenceContext
    private EntityManager entityManager;

    private final SimulatedPortfolioRepository portfolioRepository;
    private final SimulatedPortfolioDepositRepository depositRepository;
    private final SimulatedPortfolioWithdrawalRepository withdrawalRepository;
    private final SimulatedPortfolioTransactionRepository transactionRepository;
    private final SimulatedOptionsTransactionRepository optionsTransactionRepository;
    private final SimulatedOptionsPositionRepository optionsPositionRepository;

    private final PortfolioDocumentBuilder portfolioBuilder;
    private final DepositDocumentBuilder depositBuilder;
    private final WithdrawalDocumentBuilder withdrawalBuilder;
    private final StockTransactionDocumentBuilder stockTransactionBuilder;
    private final OptionsTransactionDocumentBuilder optionsTransactionBuilder;
    private final OptionsPositionDocumentBuilder optionsPositionBuilder;

    public GenerateDocumentsResponse generatePortfolioDocuments() {

        List<SimulatedPortfolio> portfolios =
                portfolioRepository.findAll();

        int generated =
                generator.generateDocuments(
                        portfolios,
                        portfolioBuilder);

        return buildResponse(portfolios.size(), generated);
    }

    public GenerateDocumentsResponse generateDepositDocuments() {

        List<SimulatedPortfolioDeposit> deposits =
                depositRepository.findAll();

        int generated =
                generator.generateDocuments(
                        deposits,
                        depositBuilder);

        return buildResponse(deposits.size(), generated);
    }

    public GenerateDocumentsResponse generateWithdrawalDocuments() {

        List<SimulatedPortfolioWithdrawal> withdrawals =
                withdrawalRepository.findAll();

        int generated =
                generator.generateDocuments(
                        withdrawals,
                        withdrawalBuilder);

        return buildResponse(withdrawals.size(), generated);
    }

    public GenerateDocumentsResponse generateStockTransactionDocuments() {

        List<SimulatedPortfolioTransaction> transactions =
                transactionRepository.findAll();

        int generated =
                generator.generateDocuments(
                        transactions,
                        stockTransactionBuilder);

        return buildResponse(transactions.size(), generated);
    }

    public GenerateDocumentsResponse generateOptionsTransactionDocuments() {

        List<SimulatedOptionsTransaction> transactions =
                optionsTransactionRepository.findAll();

        int generated =
                generator.generateDocuments(
                        transactions,
                        optionsTransactionBuilder);

        return buildResponse(transactions.size(), generated);
    }

    public GenerateDocumentsResponse generateOptionsPositionDocuments() {

        List<SimulatedOptionsPosition> positions =
                optionsPositionRepository.findAll();

        int generated =
                generator.generateDocuments(
                        positions,
                        optionsPositionBuilder);

        return buildResponse(positions.size(), generated);
    }

    public GenerateDocumentsResponse regenerateOptionsPositionDocuments() {

        List<SimulatedOptionsPosition> positions =
                optionsPositionRepository.findAll();

        int regenerated =
                generator.regenerateDocuments(
                        positions,
                        optionsPositionBuilder);

        return buildResponse(positions.size(), regenerated);
    }

    public GenerateDocumentsResponse generateAllDocuments() {

        int totalRecords = 0;
        int totalGenerated = 0;

        GenerateDocumentsResponse portfolioResp = generatePortfolioDocuments();
        totalRecords += portfolioResp.getTotalRecords();
        totalGenerated += portfolioResp.getGeneratedDocuments();

        GenerateDocumentsResponse depositResp = generateDepositDocuments();
        totalRecords += depositResp.getTotalRecords();
        totalGenerated += depositResp.getGeneratedDocuments();

        GenerateDocumentsResponse withdrawalResp = generateWithdrawalDocuments();
        totalRecords += withdrawalResp.getTotalRecords();
        totalGenerated += withdrawalResp.getGeneratedDocuments();

        GenerateDocumentsResponse stockResp = generateStockTransactionDocuments();
        totalRecords += stockResp.getTotalRecords();
        totalGenerated += stockResp.getGeneratedDocuments();

        GenerateDocumentsResponse optionsTxResp = generateOptionsTransactionDocuments();
        totalRecords += optionsTxResp.getTotalRecords();
        totalGenerated += optionsTxResp.getGeneratedDocuments();

        GenerateDocumentsResponse optionsPosResp = generateOptionsPositionDocuments();
        totalRecords += optionsPosResp.getTotalRecords();
        totalGenerated += optionsPosResp.getGeneratedDocuments();

        return GenerateDocumentsResponse.builder()
                .totalRecords(totalRecords)
                .generatedDocuments(totalGenerated)
                .skippedDocuments(totalRecords - totalGenerated)
                .build();
    }

    private GenerateDocumentsResponse buildResponse(
            int total,
            int generated) {

        return GenerateDocumentsResponse.builder()
                .totalRecords(total)
                .generatedDocuments(generated)
                .skippedDocuments(total - generated)
                .build();
    }
}
