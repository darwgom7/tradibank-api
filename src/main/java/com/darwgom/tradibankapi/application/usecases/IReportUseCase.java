package com.darwgom.tradibankapi.application.usecases;

import com.darwgom.tradibankapi.application.dto.MessageDTO;
import com.darwgom.tradibankapi.application.dto.ReportDTO;
import com.darwgom.tradibankapi.application.dto.ReportInputDTO;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

public interface IReportUseCase {
    byte[] generateMonthlyStatement(Long accountId, YearMonth month) throws IOException;
    MessageDTO saveMonthlyStatement(byte[] generateMonthlyStatement);

}

