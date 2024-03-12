package com.darwgom.tradibankapi.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInputDTO {
    private String name;
    private String username;
    private String password;
    private String role;
    private String identityDocument;
}