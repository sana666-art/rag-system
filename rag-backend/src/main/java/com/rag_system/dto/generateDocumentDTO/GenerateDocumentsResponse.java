package com.rag_system.dto.generateDocumentDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateDocumentsResponse {

    private int totalRecords;

    private int generatedDocuments;

    private int skippedDocuments;

}
