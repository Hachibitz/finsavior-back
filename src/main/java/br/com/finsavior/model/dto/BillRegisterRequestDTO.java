package br.com.finsavior.model.dto;

public class BillRegisterRequestDTO {
    private String billType;
    private String billDate;
    private String billName;
    private double billValue;
    private String billDescription;
    private String billTable;

    public BillRegisterRequestDTO() {
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public double getBillValue() {
        return billValue;
    }

    public void setBillValue(double billValue) {
        this.billValue = billValue;
    }

    public String getBillDescription() {
        return billDescription;
    }

    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
    }

    public String getBillTable() {
        return billTable;
    }

    public void setBillTable(String billTable) {
        this.billTable = billTable;
    }
}
