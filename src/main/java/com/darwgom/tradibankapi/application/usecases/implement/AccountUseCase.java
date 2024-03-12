package com.darwgom.tradibankapi.application.usecases.implement;

import com.darwgom.tradibankapi.application.dto.AccountBalanceDTO;
import com.darwgom.tradibankapi.application.dto.AccountDTO;
import com.darwgom.tradibankapi.application.dto.AccountInputDTO;
import com.darwgom.tradibankapi.application.dto.MessageDTO;
import com.darwgom.tradibankapi.application.usecases.IAccountUseCase;
import com.darwgom.tradibankapi.domain.entities.Account;
import com.darwgom.tradibankapi.domain.entities.Customer;
import com.darwgom.tradibankapi.domain.enums.AccountTypeEnum;
import com.darwgom.tradibankapi.domain.exceptions.EntityNotFoundException;
import com.darwgom.tradibankapi.domain.exceptions.IllegalParamException;
import com.darwgom.tradibankapi.domain.repositories.AccountRepository;
import com.darwgom.tradibankapi.domain.repositories.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountUseCase implements IAccountUseCase {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Map<String, String> typeNormalizationMap = Map.of(
            "SAVINGS", "SAVINGS",
            "CUENTAAHORROS", "SAVINGS",
            "AHORROS", "SAVINGS",
            "CURRENT", "CURRENT",
            "CUENTACORRIENTE", "CURRENT",
            "CORRIENTE", "CURRENT"
    );

    @Override
    public AccountDTO createAccount(AccountInputDTO accountInputDTO) {
        Customer customer = customerRepository.findById(accountInputDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + accountInputDTO.getCustomerId()));

        Account newAccount = new Account();
        String normalizedType = normalizeAccountType(accountInputDTO.getAccountType());
        accountInputDTO.setAccountType(normalizedType);
        newAccount.setAccountType(AccountTypeEnum.valueOf(accountInputDTO.getAccountType()));
        newAccount.setBalance(accountInputDTO.getInitialDeposit() != null ? accountInputDTO.getInitialDeposit() : BigDecimal.ZERO);
        newAccount.setCustomer(customer);

        Account savedAccount = accountRepository.save(newAccount);
        return modelMapper.map(savedAccount, AccountDTO.class);
    }

    public AccountBalanceDTO getAccountBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with number: " + accountId));
        return new AccountBalanceDTO(account.getBalance());
    }

    @Override
    public AccountDTO getAccountDetails(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + accountId));
        return modelMapper.map(account, AccountDTO.class);
    }

    @Override
    public MessageDTO deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + accountId));
        accountRepository.delete(account);
        return new MessageDTO("Account successfully deleted");
    }

    @Override
    public List<AccountDTO> listAccountsByCustomerId(Long customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(account -> modelMapper.map(account, AccountDTO.class))
                .collect(Collectors.toList());
    }

    private String normalizeAccountType(String type) {
        if (type == null) {
            throw new IllegalParamException("Account type cannot be null");
        }
        String normalizedType = typeNormalizationMap.get(type.toUpperCase());
        if (normalizedType == null) {
            throw new IllegalParamException("Invalid account type: " + type);
        }
        return normalizedType;
    }

}
