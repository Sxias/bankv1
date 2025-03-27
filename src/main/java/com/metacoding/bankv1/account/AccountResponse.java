package com.metacoding.bankv1.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

public class AccountResponse {

    @AllArgsConstructor
    @Data
    public static class DetailDTO {
        private int accountNumber;
        private int accountBalance;
        private String accountOwner;
        private String createdAt; // substr로 잘랐으므로 String 타입
        private int wNumber;
        private int dNumber;
        private int amount;
        private int balance;
        private String type;
    }

    @AllArgsConstructor
    @Data
    public static class TransferUserDTO {
        private int accountNumber;
        private int accountBalance;
        private String accountOwner;
    }

    @AllArgsConstructor
    @Data
    public static class TransferListDTO {
        private String createdAt; // substr로 잘랐으므로 String 타입
        private int wNumber;
        private int dNumber;
        private int amount;
        private int balance;
        private String type;
    }

    @Data
    public static class DetailDTO2 {
        private int accountNumber;
        private int accountBalance;
        private String accountOwner;

        private List<TransferListDTO> detailList;

        public DetailDTO2(TransferUserDTO transferUserDTO, List<TransferListDTO> detailList) {
            this.accountNumber = transferUserDTO.getAccountNumber();
            this.accountBalance = transferUserDTO.getAccountBalance();
            this.accountOwner = transferUserDTO.getAccountOwner();
            this.detailList = detailList;
        }
    }
}