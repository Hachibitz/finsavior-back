package br.com.finsavior.controller;

import br.com.finsavior.model.dto.AiAdviceDTO;
import br.com.finsavior.model.dto.AiAnalysisResponseDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.service.AiAdviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ai-advice")
public class AiAdviceController {

    @Autowired
    AiAdviceService service;

    @PostMapping("/generate-ai-advice-and-insights")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponseDTO> generateAiAdviceAndInsights(@RequestBody AiAdviceDTO aiAdvice) {
        return service.generateAiAdviceAndInsights(aiAdvice);
    }

    @GetMapping("/get-ai-advice-and-insights")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AiAnalysisResponseDTO>> getAiAdviceAndInsights() {
        return service.getAiAnalysisList();
    }
}
