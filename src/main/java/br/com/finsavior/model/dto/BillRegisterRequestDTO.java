package br.com.finsavior.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BillRegisterRequestDTO {
    private long id;
    private long userId;
    private String billType;
    private String billDate;
    private String billName;
    private double billValue;
    private String billDescription;
    private String billTable;
}
