package br.com.finsavior.model.dto;

import br.com.finsavior.grpc.tables.PaymentCardTableData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardTableDataResponseDTO {
    private List<PaymentCardTableData> paymentCardTableDataList;
}
