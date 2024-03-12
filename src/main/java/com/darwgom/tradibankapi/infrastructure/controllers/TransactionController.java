package com.darwgom.tradibankapi.infrastructure.controllers;

import com.darwgom.tradibankapi.application.dto.AccountDTO;
import com.darwgom.tradibankapi.application.dto.AccountInputDTO;
import com.darwgom.tradibankapi.application.dto.TransactionDTO;
import com.darwgom.tradibankapi.application.dto.TransactionInputDTO;
import com.darwgom.tradibankapi.application.usecases.IAccountUseCase;
import com.darwgom.tradibankapi.application.usecases.ITransactionUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    @Autowired
    private ITransactionUseCase transactionUseCase;

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionInputDTO transactionInputDTO) {
        TransactionDTO transactionDTO = transactionUseCase.createTransaction(transactionInputDTO);
        return new ResponseEntity<>(transactionDTO, HttpStatus.CREATED);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Page<TransactionDTO>> getRecentTransactions(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate,desc") String[] sortStr) {

        String[] sortParams = sortStr[0].split(",");
        Sort.Direction direction = Sort.DEFAULT_DIRECTION;

        if (sortParams.length > 1) {
            direction = Sort.Direction.fromString(sortParams[1]);
        }

        Sort sort = Sort.by(direction, sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TransactionDTO> transactionPage = transactionUseCase.getRecentTransactionsForAccount(accountId, pageable);
        return new ResponseEntity<>(transactionPage, HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(@RequestBody TransactionInputDTO transactionInput) {
        TransactionDTO transaction = transactionUseCase.withdraw(transactionInput.getAccountId(), transactionInput.getAmount());
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDTO> deposit(@RequestBody TransactionInputDTO transactionInput) {
        TransactionDTO transaction = transactionUseCase.deposit(transactionInput.getAccountId(), transactionInput.getAmount());
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

}
