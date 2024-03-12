package com.darwgom.tradibankapi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDTO {

    private String username;
    private String password;
    @JsonProperty("roletype")
    private String roleType;

}
