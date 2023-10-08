package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.maintable.*;
import br.com.finsavior.model.dto.BillRegisterRequestDTO;
import br.com.finsavior.model.dto.BillRegisterResponseDTO;
import br.com.finsavior.model.dto.MainTableDataResponseDTO;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.repository.MainTableRepository;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.service.BillRegisterService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BillRegisterServiceImpl implements BillRegisterService {

    @Autowired
    MainTableRepository repository;

    @Autowired
    UserRepository userRepository;

    //@GrpcClient("main-table-service")
    //MainServiceGrpc.MainServiceBlockingStub mainServiceBlockingStub;

    private final TableDataServiceGrpc.TableDataServiceBlockingStub tableDataServiceBlockingStub;

    public BillRegisterServiceImpl() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        tableDataServiceBlockingStub = TableDataServiceGrpc.newBlockingStub(channel);
    }

    Logger logger = LoggerFactory.getLogger(BillRegisterServiceImpl.class);

    @Override
    public ResponseEntity<BillRegisterResponseDTO> billRegister(BillRegisterRequestDTO billRegisterRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        BillRegisterRequest mainTableRequestDTO = BillRegisterRequest.newBuilder()
                .setUserId(user.getId())
                .setBillType(billRegisterRequestDTO.getBillType())
                .setBillDate(billRegisterRequestDTO.getBillDate())
                .setBillDescription(billRegisterRequestDTO.getBillDescription())
                .setBillName(billRegisterRequestDTO.getBillName())
                .setBillValue(billRegisterRequestDTO.getBillValue())
                .build();

        try {
            BillRegisterResponse billRegisterResponse = tableDataServiceBlockingStub.billRegister(mainTableRequestDTO);
            BillRegisterResponseDTO response = new BillRegisterResponseDTO(billRegisterResponse.getStatus(), billRegisterResponse.getMessage());
            logger.info("Registro de tabela principal salvo.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info(e.getMessage());
            BillRegisterResponseDTO response = new BillRegisterResponseDTO(HttpResponseStatus.INTERNAL_SERVER_ERROR.toString(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> loadMainTableData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        MainTableDataRequest mainTableDataRequest = MainTableDataRequest.newBuilder()
                .setUserId(user.getId())
                .build();
        ModelMapper modelMapper = new ModelMapper();

        try {
            MainTableDataResponse mainTableDataResponse = tableDataServiceBlockingStub.loadMainTableData(mainTableDataRequest);
            MainTableDataResponseDTO response = modelMapper.map(mainTableDataResponse, MainTableDataResponseDTO.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Falha ao carregar dados da tabela principal: "+e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha ao carregar dados da tabela principal: "+e.getMessage());
        }
    }
}
