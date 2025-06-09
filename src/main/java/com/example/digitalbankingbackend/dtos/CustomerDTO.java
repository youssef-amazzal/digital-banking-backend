package com.example.digitalbankingbackend.dtos;

import lombok.Data;
import java.util.Date;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private Date createdAt;
}