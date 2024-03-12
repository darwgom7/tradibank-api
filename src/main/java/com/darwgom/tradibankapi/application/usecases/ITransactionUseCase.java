package com.darwgom.tradibankapi.application.usecases;

import com.darwgom.tradibankapi.application.dto.ReportDTO;
import com.darwgom.tradibankapi.application.dto.TransactionDTO;
import com.darwgom.tradibankapi.application.dto.TransactionInputDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public interface ITransactionUseCase {

    Page<TransactionDTO> getRecentTransactionsForAccount(Long accountId, Pageable pageable);

    TransactionDTO deposit(Long accountId, BigDecimal amount);
    TransactionDTO withdraw(Long accountId, BigDecimal amount);

}

