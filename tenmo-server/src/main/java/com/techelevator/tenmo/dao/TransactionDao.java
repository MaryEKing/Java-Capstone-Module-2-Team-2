package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransactionDTO;

import java.util.List;

public interface TransactionDao {
    String transferAmount(TransactionDTO transactionDTO);

    List<TransactionDTO> getAllTransactions(Long userId);
}
