package com.darwgom.tradibankapi.application.dto;

import com.darwgom.tradibankapi.domain.enums.ReportTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private Long id;
    private ReportTypeEnum reportType;
    private Date creationDate;
    private String details;

}
