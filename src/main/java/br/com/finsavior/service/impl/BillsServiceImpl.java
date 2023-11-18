package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.tables.*;
import br.com.finsavior.model.dto.*;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.repository.MainTableRepository;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.service.BillsService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
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
public class BillsServiceImpl implements BillsService {

    @Autowired
    MainTableRepository repository;

    @Autowired
    UserRepository userRepository;

    //@GrpcClient("main-table-service")
    //MainServiceGrpc.MainServiceBlockingStub mainServiceBlockingStub;

    private final TableDataServiceGrpc.TableDataServiceBlockingStub tableDataServiceBlockingStub;

    public BillsServiceImpl() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        tableDataServiceBlockingStub = TableDataServiceGrpc.newBlockingStub(channel);
    }

    Logger logger = LoggerFactory.getLogger(BillsServiceImpl.class);

    @Override
    public ResponseEntity<BillRegisterResponseDTO> billRegister(BillRegisterRequestDTO billRegisterRequestDTO, boolean isRecurrent) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());
        String billType = billRegisterRequestDTO.getBillType() == null ? "" : billRegisterRequestDTO.getBillType();

        BillRegisterRequest dataRegisterRequest = BillRegisterRequest.newBuilder()
                .setUserId(user.getId())
                .setBillType(billType)
                .setBillDate(billRegisterRequestDTO.getBillDate())
                .setBillDescription(billRegisterRequestDTO.getBillDescription())
                .setBillName(billRegisterRequestDTO.getBillName())
                .setBillValue(billRegisterRequestDTO.getBillValue())
                .setBillTable(billRegisterRequestDTO.getBillTable())
                .setIsRecurrent(isRecurrent)
                .build();

        try {
            BillRegisterResponse billRegisterResponse = tableDataServiceBlockingStub.billRegister(dataRegisterRequest);
            BillRegisterResponseDTO response = new BillRegisterResponseDTO(billRegisterResponse.getStatus(), billRegisterResponse.getMessage());
            logger.info("Registro de tabela principal salvo.");
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            logger.error(e.getStatus().getDescription());
            BillRegisterResponseDTO response = new BillRegisterResponseDTO(HttpResponseStatus.INTERNAL_SERVER_ERROR.toString(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> loadMainTableData(String billDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        MainTableDataRequest mainTableDataRequest = MainTableDataRequest.newBuilder()
                .setUserId(user.getId())
                .setBillDate(billDate)
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

    @Override
    public ResponseEntity<?> loadCardTableData(String billDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        CardTableDataRequest cardTableDataRequest = CardTableDataRequest.newBuilder()
                .setUserId(user.getId())
                .setBillDate(billDate)
                .build();
        ModelMapper modelMapper = new ModelMapper();

        try {
            CardTableDataResponse cardTableDataResponse = tableDataServiceBlockingStub.loadCardTableData(cardTableDataRequest);
            CardTableDataResponseDTO response = modelMapper.map(cardTableDataResponse, CardTableDataResponseDTO.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Falha ao carregar dados da tabela de cartão de crédito: "+e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha ao carregar dados da tabela de cartão de crédito: "+e.getMessage());
        }
    }

    @Override
    public ResponseEntity<GenericResponseDTO> deleteItemFromMainTable(Long itemId) {
        DeleteItemFromTableRequest deleteItemFromTableRequest = DeleteItemFromTableRequest.newBuilder()
                .setId(itemId)
                .build();
        try {
            GenericResponse genericResponse = tableDataServiceBlockingStub.deleteItemFromMainTable(deleteItemFromTableRequest);
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.OK.toString(), genericResponse.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Falha ao excluir item da base: "+e.getMessage());
            e.printStackTrace();
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Falha ao excluir item da tabela principal da base: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<GenericResponseDTO> deleteItemFromCardTable(Long itemId) {
        DeleteItemFromTableRequest deleteItemFromTableRequest = DeleteItemFromTableRequest.newBuilder()
                .setId(itemId)
                .build();
        try {
            GenericResponse genericResponse = tableDataServiceBlockingStub.deleteItemFromCardTable(deleteItemFromTableRequest);
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.OK.toString(), genericResponse.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Falha ao excluir item da base: "+e.getMessage());
            e.printStackTrace();
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Falha ao excluir item da tabela de cartões da base: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<GenericResponseDTO> editItemFromMainTable(BillRegisterRequestDTO billRegisterRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        BillRegisterRequest billRegisterRequest = BillRegisterRequest.newBuilder()
                .setBillDate(billRegisterRequestDTO.getBillDate())
                .setBillDescription(billRegisterRequestDTO.getBillDescription())
                .setBillTable(billRegisterRequestDTO.getBillTable())
                .setBillName(billRegisterRequestDTO.getBillName())
                .setBillType(billRegisterRequestDTO.getBillType())
                .setBillValue(billRegisterRequestDTO.getBillValue())
                .setUserId(user.getId())
                .setIsRecurrent(false)
                .setIsPaid(billRegisterRequestDTO.isPaid())
                .build();

        BillUpdateRequest billUpdateRequest = BillUpdateRequest.newBuilder()
                .setBill(billRegisterRequest)
                .setId(billRegisterRequestDTO.getId())
                .build();

        try {
            GenericResponse genericResponse = tableDataServiceBlockingStub.editItemFromMainTable(billUpdateRequest);
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.OK.toString(), genericResponse.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Falha ao editar item da tabela principal: "+e.getMessage());
            e.printStackTrace();
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Falha ao editar item da tabela principal: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<GenericResponseDTO> editItemFromCardTable(BillRegisterRequestDTO billRegisterRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        BillRegisterRequest billRegisterRequest = BillRegisterRequest.newBuilder()
                .setBillDate(billRegisterRequestDTO.getBillDate())
                .setBillDescription(billRegisterRequestDTO.getBillDescription())
                .setBillTable(billRegisterRequestDTO.getBillTable())
                .setBillName(billRegisterRequestDTO.getBillName())
                .setBillValue(billRegisterRequestDTO.getBillValue())
                .setUserId(user.getId())
                .setIsRecurrent(false)
                .build();

        BillUpdateRequest billUpdateRequest = BillUpdateRequest.newBuilder()
                .setBill(billRegisterRequest)
                .setId(billRegisterRequestDTO.getId())
                .build();

        try {
            GenericResponse genericResponse = tableDataServiceBlockingStub.editItemFromCardTable(billUpdateRequest);
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.OK.toString(), genericResponse.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Falha ao editar item da tabela de detalhamento de cartões: "+e.getMessage());
            e.printStackTrace();
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Falha ao editar item da tabela de detalhamento de cartões: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
