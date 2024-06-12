package br.com.finsavior.service;

import br.com.finsavior.model.dto.AiAdviceDTO;
import br.com.finsavior.model.dto.AiAdviceResponseDTO;
import br.com.finsavior.model.dto.AiAnalysisResponseDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AiAdviceService {
    public ResponseEntity<AiAdviceResponseDTO> generateAiAdviceAndInsights(AiAdviceDTO aiAdvice);
    public ResponseEntity<List<AiAnalysisResponseDTO>> getAiAnalysisList();
    public ResponseEntity<GenericResponseDTO> deleteAnalysis(Long analysisId);
}
