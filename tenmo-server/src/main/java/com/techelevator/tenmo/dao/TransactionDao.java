package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransactionDTO;

public interface TransactionDao {
    String transferAmount(TransactionDTO transactionDTO);
}
