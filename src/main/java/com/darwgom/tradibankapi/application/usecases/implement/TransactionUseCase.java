package com.darwgom.tradibankapi.application.usecases.implement;

import com.darwgom.tradibankapi.application.dto.ReportDTO;
import com.darwgom.tradibankapi.application.dto.TransactionDTO;
import com.darwgom.tradibankapi.application.dto.TransactionInputDTO;
import com.darwgom.tradibankapi.application.usecases.ITransactionUseCase;
import com.darwgom.tradibankapi.domain.entities.Account;
import com.darwgom.tradibankapi.domain.entities.Transaction;
import com.darwgom.tradibankapi.domain.enums.TransactionTypeEnum;
import com.darwgom.tradibankapi.domain.exceptions.EntityNotFoundException;
import com.darwgom.tradibankapi.domain.exceptions.IllegalParamException;
import com.darwgom.tradibankapi.domain.repositories.AccountRepository;
import com.darwgom.tradibankapi.domain.repositories.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class TransactionUseCase implements ITransactionUseCase {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public TransactionDTO deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + accountId));

        account.setBalance(account.getBalance().add(amount));
        Account updatedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(updatedAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionTypeEnum.DEPOSIT);
        transaction.setTransactionDate(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return modelMapper.map(savedTransaction, TransactionDTO.class);
    }

    @Override
    public TransactionDTO withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + accountId));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalParamException("Insufficient funds for withdrawal.");
        }

        account.setBalance(account.getBalance().subtract(amount));
        Account updatedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(updatedAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionTypeEnum.WITHDRAWAL);
        transaction.setTransactionDate(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return modelMapper.map(savedTransaction, TransactionDTO.class);
    }


    @Override
    public Page<TransactionDTO> getRecentTransactionsForAccount(Long accountId, Pageable pageable) {
        Page<Transaction> transactionPage = transactionRepository.findByAccountId(accountId, pageable);
        return transactionPage.map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
    }


}

