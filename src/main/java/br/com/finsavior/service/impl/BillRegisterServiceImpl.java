package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.maintable.MainServiceGrpc;
import br.com.finsavior.grpc.maintable.BillRegisterRequest;
import br.com.finsavior.grpc.maintable.BillRegisterResponse;
import br.com.finsavior.model.dto.BillRegisterRequestDTO;
import br.com.finsavior.model.dto.BillRegisterResponseDTO;
import br.com.finsavior.repository.MainTableRepository;
import br.com.finsavior.service.BillRegisterService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BillRegisterServiceImpl implements BillRegisterService {

    @Autowired
    MainTableRepository repository;

    //@GrpcClient("main-table-service")
    //MainServiceGrpc.MainServiceBlockingStub mainServiceBlockingStub;

    private final MainServiceGrpc.MainServiceBlockingStub mainServiceBlockingStub;

    public BillRegisterServiceImpl() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        mainServiceBlockingStub = MainServiceGrpc.newBlockingStub(channel);
    }

    Logger logger = LoggerFactory.getLogger(BillRegisterServiceImpl.class);

    @Override
    public ResponseEntity<BillRegisterResponseDTO> billRegister(BillRegisterRequestDTO billRegisterRequestDTO) {
        BillRegisterRequest mainTableRequestDTO = BillRegisterRequest.newBuilder()
                .setBillType(billRegisterRequestDTO.getBillType())
                .setBillDate(billRegisterRequestDTO.getBillDate())
                .setBillDescription(billRegisterRequestDTO.getBillDescription())
                .setBillName(billRegisterRequestDTO.getBillName())
                .setBillValue(billRegisterRequestDTO.getBillValue())
                .build();

        try {
            BillRegisterResponse billRegisterResponse = mainServiceBlockingStub.billRegister(mainTableRequestDTO);
            BillRegisterResponseDTO response = new BillRegisterResponseDTO(billRegisterResponse.getStatus(), billRegisterResponse.getMessage());

            logger.info("Registro de tabela principal salvo.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }
}
