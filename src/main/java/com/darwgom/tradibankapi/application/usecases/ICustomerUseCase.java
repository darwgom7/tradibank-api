package com.darwgom.tradibankapi.application.usecases;

import com.darwgom.tradibankapi.application.dto.CustomerDTO;
import com.darwgom.tradibankapi.application.dto.CustomerInputDTO;
import com.darwgom.tradibankapi.application.dto.MessageDTO;

import java.util.List;

public interface ICustomerUseCase {
    CustomerDTO createCustomer(CustomerInputDTO customerInputDTO);
    CustomerDTO createCustomerUser(CustomerInputDTO customerInputDTO);
    CustomerDTO getCustomerDetails(Long customerId);
    MessageDTO deleteCustomer(Long customerId);
    List<CustomerDTO> listAllCustomers();
}
