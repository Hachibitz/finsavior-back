package br.com.finsavior.model.dto;

import br.com.finsavior.grpc.maintable.BillRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillRegisterResponseDTO {
    private String status;
    private BillRegisterRequest message;
}
