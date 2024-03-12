package com.darwgom.tradibankapi.application.usecases.implement;

import com.darwgom.tradibankapi.application.dto.CustomerDTO;
import com.darwgom.tradibankapi.application.dto.CustomerInputDTO;
import com.darwgom.tradibankapi.application.dto.MessageDTO;
import com.darwgom.tradibankapi.application.usecases.ICustomerUseCase;
import com.darwgom.tradibankapi.domain.entities.Customer;
import com.darwgom.tradibankapi.domain.entities.User;
import com.darwgom.tradibankapi.domain.enums.RoleTypeEnum;
import com.darwgom.tradibankapi.domain.exceptions.EntityNotFoundException;
import com.darwgom.tradibankapi.domain.exceptions.IllegalParamException;
import com.darwgom.tradibankapi.domain.exceptions.UsernameAlreadyExistsException;
import com.darwgom.tradibankapi.domain.repositories.CustomerRepository;
import com.darwgom.tradibankapi.domain.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerUseCase implements ICustomerUseCase {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Map<String, String> typeNormalizationMap = Map.of(
            "ROLE_ADMIN", "ROLE_ADMIN",
            "ROLEADMIN", "ROLE_ADMIN",
            "ADMIN", "ROLE_ADMIN",
            "ROLE_USER", "ROLE_USER",
            "ROLEUSER", "ROLE_USER",
            "USER", "ROLE_USER"
    );


    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerInputDTO customerInputDTO) {
        if (userRepository.existsByUsername(customerInputDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        User newUser = new User();
        newUser.setUsername(customerInputDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(customerInputDTO.getPassword()));
        newUser.setRoleType(RoleTypeEnum.ROLE_USER);
        User savedUser = userRepository.save(newUser);

        Customer newCustomer = new Customer();
        newCustomer.setName(customerInputDTO.getName());
        newCustomer.setIdentityDocument(customerInputDTO.getIdentityDocument());
        newCustomer.setUser(savedUser);
        Customer savedCustomer = customerRepository.save(newCustomer);

        return modelMapper.map(savedCustomer, CustomerDTO.class);
    }

    @Override
    @Transactional
    public CustomerDTO createCustomerUser(CustomerInputDTO customerInputDTO) {
        if (userRepository.existsByUsername(customerInputDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        String normalizedType = normalizeRoleType(customerInputDTO.getRole());
        customerInputDTO.setRole(normalizedType);
        User newUser = new User();
        newUser.setUsername(customerInputDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(customerInputDTO.getPassword()));
        newUser.setRoleType(RoleTypeEnum.valueOf(customerInputDTO.getRole()));
        User savedUser = userRepository.save(newUser);

        Customer newCustomer = new Customer();
        newCustomer.setName(customerInputDTO.getName());
        newCustomer.setIdentityDocument(customerInputDTO.getIdentityDocument());
        newCustomer.setUser(savedUser);
        Customer savedCustomer = customerRepository.save(newCustomer);

        return modelMapper.map(savedCustomer, CustomerDTO.class);
    }

    @Override
    public CustomerDTO getCustomerDetails(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    @Transactional
    public MessageDTO deleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));

        if (customer.getUser() != null) {
            userRepository.delete(customer.getUser());
        }

        customerRepository.delete(customer);
        return new MessageDTO("Customer deleted successfully");
    }


    @Override
    public List<CustomerDTO> listAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }

    private String normalizeRoleType(String type) {
        if (type == null) {
            throw new IllegalParamException("Role type cannot be null");
        }
        String normalizedType = typeNormalizationMap.get(type.toUpperCase());
        if (normalizedType == null) {
            throw new IllegalParamException("Invalid role type: " + type);
        }
        return normalizedType;
    }

}
