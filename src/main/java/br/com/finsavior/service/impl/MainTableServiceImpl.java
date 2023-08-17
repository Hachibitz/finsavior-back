package br.com.finsavior.service.impl;

import br.com.finsavior.model.dto.MainTableRegisterRequestDTO;
import br.com.finsavior.model.entities.MainTable;
import br.com.finsavior.model.dto.MainTableRegisterResponseDTO;
import br.com.finsavior.repository.MainTableRepository;
import br.com.finsavior.service.MainTableService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MainTableServiceImpl implements MainTableService {

    @Autowired
    MainTableRepository repository;

    Logger logger = LoggerFactory.getLogger(MainTableServiceImpl.class);

    @Override
    public ResponseEntity<MainTableRegisterResponseDTO> billRegister(MainTableRegisterRequestDTO mainTableRegisterRequestDTO) {
        ModelMapper modelMapper = new ModelMapper();
        MainTable register = modelMapper.map(mainTableRegisterRequestDTO ,MainTable.class);
        repository.save(register);

        MainTableRegisterResponseDTO genericResponse = new MainTableRegisterResponseDTO();
        genericResponse.setStatus(HttpStatus.OK.toString());
        genericResponse.setMessage(mainTableRegisterRequestDTO);

        logger.info("Registro de tabela principal salvo.");

        return ResponseEntity.ok(genericResponse);
    }
}
