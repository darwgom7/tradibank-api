package com.darwgom.tradibankapi.domain.repositories;

import com.darwgom.tradibankapi.domain.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);

    /*@Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND FUNCTION('MONTH', t.transactionDate) = :month AND FUNCTION('YEAR', t.transactionDate) = :year")
    List<Transaction> findByAccountIdAndMonthAndYear(@Param("accountId") Long accountId, @Param("month") int month, @Param("year") int year);*/

    /*@Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year")
    List<Transaction> findByAccountIdAndMonthAndYear(@Param("accountId") Long accountId, @Param("month") int month, @Param("year") int year);*/

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND FUNCTION('YEAR', t.transactionDate) = :year AND FUNCTION('MONTH', t.transactionDate) = :month")
    List<Transaction> findByAccountIdAndMonthAndYear(@Param("accountId") Long accountId, @Param("month") int month, @Param("year") int year);


}