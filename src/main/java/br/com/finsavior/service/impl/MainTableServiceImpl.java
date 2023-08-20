package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.maintable.MainServiceGrpc;
import br.com.finsavior.grpc.maintable.MainTableRequestDTO;
import br.com.finsavior.grpc.maintable.MainTableResponseDTO;
import br.com.finsavior.model.dto.MainTableRegisterRequestDTO;
import br.com.finsavior.model.dto.MainTableRegisterResponseDTO;
import br.com.finsavior.repository.MainTableRepository;
import br.com.finsavior.service.MainTableService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MainTableServiceImpl implements MainTableService {

    @Autowired
    MainTableRepository repository;

    //@GrpcClient("main-table-service")
    //MainServiceGrpc.MainServiceBlockingStub mainServiceBlockingStub;

    private final MainServiceGrpc.MainServiceBlockingStub mainServiceBlockingStub;

    public MainTableServiceImpl() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        mainServiceBlockingStub = MainServiceGrpc.newBlockingStub(channel);
    }

    Logger logger = LoggerFactory.getLogger(MainTableServiceImpl.class);

    @Override
    public ResponseEntity<MainTableRegisterResponseDTO> billRegister(MainTableRegisterRequestDTO mainTableRegisterRequestDTO) {
        MainTableRequestDTO mainTableRequestDTO = MainTableRequestDTO.newBuilder()
                .setBillType(mainTableRegisterRequestDTO.getBillType())
                .setBillDate(mainTableRegisterRequestDTO.getBillDate())
                .setBillDescription(mainTableRegisterRequestDTO.getBillDescription())
                .setBillName(mainTableRegisterRequestDTO.getBillName())
                .setBillValue(mainTableRegisterRequestDTO.getBillValue())
                .build();

        try {
            MainTableResponseDTO mainTableResponseDTO = mainServiceBlockingStub.billRegister(mainTableRequestDTO);
            MainTableRegisterResponseDTO response = new MainTableRegisterResponseDTO(mainTableResponseDTO.getStatus(), mainTableResponseDTO.getMessage());

            logger.info("Registro de tabela principal salvo.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }
}
