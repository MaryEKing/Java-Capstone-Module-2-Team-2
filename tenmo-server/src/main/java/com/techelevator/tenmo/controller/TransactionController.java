package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.model.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
    private JdbcTransactionDao jdbcTransactionDao;

    @Autowired
    public TransactionController(JdbcTransactionDao jdbcTransactionDao) {
        this.jdbcTransactionDao = jdbcTransactionDao;
    }

    @RequestMapping(path = "/transaction", method = RequestMethod.POST)
    public String getAllUser(@RequestBody TransactionDTO transactionDTO){
        return jdbcTransactionDao.transferAmount(transactionDTO);
    }
}
