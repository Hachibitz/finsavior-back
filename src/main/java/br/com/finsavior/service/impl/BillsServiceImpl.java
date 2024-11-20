package br.com.finsavior.service.impl;

import br.com.finsavior.exception.BillRegisterException;
import br.com.finsavior.grpc.payment.Payment;
import br.com.finsavior.grpc.tables.*;
import br.com.finsavior.grpc.tables.TableDataServiceGrpc.TableDataServiceBlockingStub;
import br.com.finsavior.model.dto.*;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.service.BillsService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BillsServiceImpl implements BillsService {

    private final UserRepository userRepository;
    private final Environment environment;

    private TableDataServiceBlockingStub tableDataServiceBlockingStub;

    @Autowired
    public BillsServiceImpl(UserRepository userRepository, Environment environment) {
        this.userRepository = userRepository;
        this.environment = environment;
    }

    @PostConstruct
    public void initialize() {
        String tableServiceHost = environment.getProperty("finsavior.table.service.host");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(tableServiceHost, 6565)
                .usePlaintext()
                .build();

        tableDataServiceBlockingStub = TableDataServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public ResponseEntity<BillRegisterResponseDTO> billRegister(BillRegisterRequestDTO billRegisterRequestDTO, boolean isRecurrent) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());
        String billType = billRegisterRequestDTO.getBillType() == null ? "" : billRegisterRequestDTO.getBillType();

        BillRegisterRequest dataRegisterRequest = BillRegisterRequest.newBuilder()
                .setUserId(user.getId())
                .setBillType(billType)
                .setBillDate(formatBillDate(billRegisterRequestDTO.getBillDate()))
                .setBillDescription(billRegisterRequestDTO.getBillDescription())
                .setBillName(billRegisterRequestDTO.getBillName())
                .setBillValue(billRegisterRequestDTO.getBillValue())
                .setBillTable(billRegisterRequestDTO.getBillTable())
                .setIsRecurrent(isRecurrent)
                .build();

        try {
            BillRegisterResponse billRegisterResponse = tableDataServiceBlockingStub.billRegister(dataRegisterRequest);
            BillRegisterResponseDTO response = new BillRegisterResponseDTO(billRegisterResponse.getStatus(), billRegisterResponse.getMessage());
            log.debug("Registro salvo na {} table.", billRegisterRequestDTO.getBillTable());
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            log.error(e.getStatus().getDescription());
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
                .setBillDate(formatBillDate(billDate))
                .build();
        ModelMapper modelMapper = new ModelMapper();

        try {
            MainTableDataResponse mainTableDataResponse = tableDataServiceBlockingStub.loadMainTableData(mainTableDataRequest);
            MainTableDataResponseDTO response = modelMapper.map(mainTableDataResponse, MainTableDataResponseDTO.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Falha ao carregar dados da tabela principal: "+e.getMessage());
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
                .setBillDate(formatBillDate(billDate))
                .build();
        ModelMapper modelMapper = new ModelMapper();

        try {
            CardTableDataResponse cardTableDataResponse = tableDataServiceBlockingStub.loadCardTableData(cardTableDataRequest);
            CardTableDataResponseDTO response = modelMapper.map(cardTableDataResponse, CardTableDataResponseDTO.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Falha ao carregar dados da tabela de cartão de crédito: "+e.getMessage());
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
            log.error("Falha ao excluir item da base: "+e.getMessage());
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
            log.error("Falha ao excluir item da base: "+e.getMessage());
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
                .setBillDate(formatBillDate(billRegisterRequestDTO.getBillDate()))
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
            log.error("Falha ao editar item da tabela principal: "+e.getMessage());
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
                .setBillDate(formatBillDate(billRegisterRequestDTO.getBillDate()))
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
            log.error("Falha ao editar item da tabela de detalhamento de cartões: "+e.getMessage());
            e.printStackTrace();
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Falha ao editar item da tabela de detalhamento de cartões: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<BillRegisterResponseDTO> cardPaymentRegister(BillRegisterRequestDTO billRegisterRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());
        String billType = billRegisterRequestDTO.getBillType() == null ? "" : billRegisterRequestDTO.getBillType();

        BillRegisterRequest dataRegisterRequest = BillRegisterRequest.newBuilder()
                .setUserId(user.getId())
                .setBillType(billType)
                .setBillDate(formatBillDate(billRegisterRequestDTO.getBillDate()))
                .setBillDescription(billRegisterRequestDTO.getBillDescription())
                .setBillName(billRegisterRequestDTO.getBillName())
                .setBillValue(billRegisterRequestDTO.getBillValue())
                .setBillTable(billRegisterRequestDTO.getBillTable())
                .setIsRecurrent(false)
                .build();

        try {
            BillRegisterResponse billRegisterResponse = tableDataServiceBlockingStub.cardPaymentRegister(dataRegisterRequest);
            BillRegisterResponseDTO response = new BillRegisterResponseDTO(billRegisterResponse.getStatus(), billRegisterResponse.getMessage());
            log.debug("Registro salvo na {} table.", billRegisterRequestDTO.getBillTable());
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            log.error("c={}, m={}, msg={}", this.getClass().getSimpleName(), "cardPaymentRegister", e.getStatus().getDescription());
            throw new BillRegisterException(e.getStatus().getDescription());
        }
    }

    @Override
    public ResponseEntity<?> loadPaymentCardTableData(String billDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        PaymentCardTableDataRequest paymentCardTableDataRequest = PaymentCardTableDataRequest.newBuilder()
                .setUserId(user.getId())
                .setBillDate(formatBillDate(billDate))
                .build();

        ModelMapper modelMapper = new ModelMapper();
        try {
            PaymentCardTableDataResponse paymentCardTableDataResponse = tableDataServiceBlockingStub.loadPaymentCardTableData(paymentCardTableDataRequest);
            PaymentCardTableDataResponseDTO response = modelMapper.map(paymentCardTableDataResponse, PaymentCardTableDataResponseDTO.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("c={}, m={}, msg={}", this.getClass().getSimpleName(), "loadPaymentCardTableData", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private String formatBillDate(String billDate) {
        if(billDate.length() == 7) {
            billDate = billDate.substring(0, 3) + " " + billDate.substring(3, 7);
        }

        return billDate;
    }
}
