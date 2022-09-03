package com.gokul.personal.AdminActivity;

import java.util.Comparator;

public class AdminData {

    private String listValue, personalName, personalValue, bankName, bankCIF, bankAccount, bankIFSC, bankMICR, bankBranch;

    public String getListValue() {
        return listValue;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getPersonalValue() {
        return personalValue;
    }

    public void setPersonalValue(String personalValue) {
        this.personalValue = personalValue;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCIF() {
        return bankCIF;
    }

    public void setBankCIF(String bankCIF) {
        this.bankCIF = bankCIF;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankIFSC() {
        return bankIFSC;
    }

    public void setBankIFSC(String bankIFSC) {
        this.bankIFSC = bankIFSC;
    }

    public String getBankMICR() {
        return bankMICR;
    }

    public void setBankMICR(String bankMICR) {
        this.bankMICR = bankMICR;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public static Comparator<AdminData> docComparatorAZ = (a1, a2) -> a1.getPersonalName().compareTo(a2.getPersonalName());

    public static Comparator<AdminData> docComparatorZA = (a1, a2) -> a2.getPersonalName().compareTo(a1.getPersonalName());

}
