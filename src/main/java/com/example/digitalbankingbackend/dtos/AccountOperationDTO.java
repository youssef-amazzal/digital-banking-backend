package com.example.digitalbankingbackend.dtos;

import com.example.digitalbankingbackend.enums.OperationType;
import lombok.Data;
import java.util.Date;

@Data
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
}