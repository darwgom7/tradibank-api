package com.darwgom.tradibankapi.infrastructure.controllers;

import com.darwgom.tradibankapi.application.dto.CustomerDTO;
import com.darwgom.tradibankapi.application.dto.CustomerInputDTO;
import com.darwgom.tradibankapi.application.dto.MessageDTO;
import com.darwgom.tradibankapi.application.usecases.ICustomerUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private ICustomerUseCase customerUseCase;

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerInputDTO customerInputDTO) {
        CustomerDTO customerDTO = customerUseCase.createCustomer(customerInputDTO);
        return new ResponseEntity<>(customerDTO, HttpStatus.CREATED);
    }

    @PostMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomerDTO> createCustomerUser(@RequestBody CustomerInputDTO customerInputDTO) {
        CustomerDTO customerDTO = customerUseCase.createCustomer(customerInputDTO);
        return new ResponseEntity<>(customerDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerDetails(@PathVariable Long customerId) {
        CustomerDTO customerDTO = customerUseCase.getCustomerDetails(customerId);
        return new ResponseEntity<>(customerDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<MessageDTO> deleteCustomer(@PathVariable Long customerId) {
        MessageDTO messageDTO = customerUseCase.deleteCustomer(customerId);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> listAllCustomers() {
        List<CustomerDTO> customers = customerUseCase.listAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }
}