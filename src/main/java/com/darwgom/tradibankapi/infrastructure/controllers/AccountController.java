package com.darwgom.tradibankapi.infrastructure.controllers;

import com.darwgom.tradibankapi.application.dto.AccountBalanceDTO;
import com.darwgom.tradibankapi.application.dto.AccountDTO;
import com.darwgom.tradibankapi.application.dto.AccountInputDTO;

import com.darwgom.tradibankapi.application.dto.MessageDTO;
import com.darwgom.tradibankapi.application.usecases.IAccountUseCase;
import com.darwgom.tradibankapi.application.usecases.implement.ReportUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("api/accounts")
public class AccountController {
    @Autowired
    private IAccountUseCase accountUseCase;

    @Autowired
    private ReportUseCase reportUseCase;

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountInputDTO accountInputDTO) {
        AccountDTO accountDTO = accountUseCase.createAccount(accountInputDTO);
        return new ResponseEntity<>(accountDTO, HttpStatus.CREATED);
    }

    @GetMapping("/statements/monthly/{accountId}")
    public ResponseEntity<byte[]> generateMonthlyStatement(@PathVariable Long accountId, @RequestParam int year, @RequestParam int month) {
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            byte[] content = reportUseCase.generateMonthlyStatement(accountId, yearMonth);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Podrías ajustar el nombre del archivo según sea necesario
            String filename = "statement-" + accountId + "-" + month + "-" + year + ".pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<AccountBalanceDTO> getAccountBalance(@PathVariable Long accountId) {
        AccountBalanceDTO accountBalanceDTO = accountUseCase.getAccountBalance(accountId);
        return new ResponseEntity<>(accountBalanceDTO, HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDTO> getAccountDetails(@PathVariable Long accountId) {
        AccountDTO accountDTO = accountUseCase.getAccountDetails(accountId);
        return new ResponseEntity<>(accountDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<MessageDTO> deleteAccount(@PathVariable Long accountId) {
        MessageDTO messageDTO = accountUseCase.deleteAccount(accountId);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDTO>> listAccountsByCustomerId(@PathVariable Long customerId) {
        List<AccountDTO> accounts = accountUseCase.listAccountsByCustomerId(customerId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }
}
