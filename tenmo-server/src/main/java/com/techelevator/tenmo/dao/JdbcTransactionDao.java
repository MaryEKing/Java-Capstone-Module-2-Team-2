package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountDTO;
import com.techelevator.tenmo.model.TransactionDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransactionDao implements TransactionDao{

    private JdbcTemplate jdbcTemplate;
    private JdbcAccountDao jdbcAccountDao;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate, JdbcAccountDao jdbcAccountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcAccountDao = jdbcAccountDao;
    }
    @Override
    public String transferAmount(TransactionDTO transactionDTO) {

        AccountDTO fromAccount = jdbcAccountDao.getAccountBalance(transactionDTO.getFromUserId());
        AccountDTO toAccount = jdbcAccountDao.getAccountBalance(transactionDTO.getToUserId());

        Double amountToTransfer = transactionDTO.getAmount();
        if(fromAccount.getBalance() >= amountToTransfer) {
            Double fromUpdatedBalance = fromAccount.getBalance() - amountToTransfer;
            Double toUpdatedBalance = toAccount.getBalance() + amountToTransfer;

            String sql = "UPDATE accounts " + "SET balance = ? " + "WHERE account_id = ?;";
            jdbcTemplate.update(sql, fromUpdatedBalance, fromAccount.getAccountId());
            jdbcTemplate.update(sql, toUpdatedBalance, toAccount.getAccountId());

            String insertSql = "INSERT INTO transfers " +
                    "(transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                    " VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";
            Long transactionId = jdbcTemplate.queryForObject(insertSql, Long.class,
                    transactionDTO.getTransactionTypeId(),
                    transactionDTO.getTransactionStatusId(),
                    fromAccount.getAccountId(),
                    toAccount.getAccountId(),
                    amountToTransfer);
            return "Transaction Success! Transfer ID: "+transactionId;
        }else {
            return "Transaction Failure! Insufficient Balance!";
        }
    }
}
