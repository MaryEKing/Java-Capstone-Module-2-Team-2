package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.model.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {
    private JdbcTransactionDao jdbcTransactionDao;

    @Autowired
    public TransactionController(JdbcTransactionDao jdbcTransactionDao) {
        this.jdbcTransactionDao = jdbcTransactionDao;
    }

    @RequestMapping(path = "/transaction", method = RequestMethod.POST)
    public String transferAmount(@RequestBody TransactionDTO transactionDTO){
        transactionDTO.setTransactionStatusId(2L);
        return jdbcTransactionDao.transferAmount(transactionDTO);
    }

    @RequestMapping(path = "/transaction", method = RequestMethod.GET)
    public List<TransactionDTO> getAllTransactions(@RequestParam Long userId){
        return jdbcTransactionDao.getAllTransactions(userId);
    }
}
