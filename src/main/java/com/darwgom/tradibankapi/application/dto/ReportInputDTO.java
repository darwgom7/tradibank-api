package com.darwgom.tradibankapi.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportInputDTO {
    private String reportType;
    private String details;
}
