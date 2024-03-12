package com.darwgom.tradibankapi.application.usecases;

import com.darwgom.tradibankapi.application.dto.AccountBalanceDTO;
import com.darwgom.tradibankapi.application.dto.AccountDTO;
import com.darwgom.tradibankapi.application.dto.AccountInputDTO;
import com.darwgom.tradibankapi.application.dto.MessageDTO;

import java.util.List;

public interface IAccountUseCase {
    AccountDTO createAccount(AccountInputDTO accountInputDTO);

    public AccountBalanceDTO getAccountBalance(Long accountId);
    AccountDTO getAccountDetails(Long accountId);
    MessageDTO deleteAccount(Long accountId);
    List<AccountDTO> listAccountsByCustomerId(Long customerId);
}
