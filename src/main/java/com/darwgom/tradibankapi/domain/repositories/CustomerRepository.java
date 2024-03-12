package com.darwgom.tradibankapi.domain.repositories;

import com.darwgom.tradibankapi.domain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
