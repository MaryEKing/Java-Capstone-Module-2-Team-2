package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountDTO;
import com.techelevator.tenmo.model.TransactionDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionDao{

    private JdbcTemplate jdbcTemplate;
    private JdbcAccountDao jdbcAccountDao;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate, JdbcAccountDao jdbcAccountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcAccountDao = jdbcAccountDao;
    }

    // To make the transaction
    @Override
    public String transferAmount(TransactionDTO transactionDTO) {
        // getting fromAccount by fromUserId
        AccountDTO fromAccount = jdbcAccountDao.getAccount(transactionDTO.getFromUserId());
        // getting toAccount by toUserId
        AccountDTO toAccount = jdbcAccountDao.getAccount(transactionDTO.getToUserId());


        //extracting the amount to transfer from transactionDto
        Double amountToTransfer = transactionDTO.getAmount();
        //checking if the amount is lower than the existing balance of the user, if so it will let the user to transfer the money
        //if the balance is greater than the amount to send
        if(fromAccount.getBalance() >= amountToTransfer) {
            //calculating the fromAccount balance
            Double fromUpdatedBalance = fromAccount.getBalance() - amountToTransfer;
            //calculating the toAccount balance
            Double toUpdatedBalance = toAccount.getBalance() + amountToTransfer;

            //updating the both accounts with updated balance
            String sql = "UPDATE accounts " + "SET balance = ? " + "WHERE account_id = ?;";
            jdbcTemplate.update(sql, fromUpdatedBalance, fromAccount.getAccountId());
            jdbcTemplate.update(sql, toUpdatedBalance, toAccount.getAccountId());

            //registering the transaction in transfers table
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

    @Override
    public List<TransactionDTO> getAllTransactions(Long userId) {
        List<TransactionDTO> transactions = new ArrayList<>();
        String sql = "select " +
                "transfer_id, " +
                "transfer_type_desc, " +
                "transfer_status_desc, " +
                "account_from, " +
                "account_to, " +
                "amount " +
                "from transfers " +
                "join accounts on (accounts.account_id = transfers.account_from or accounts.account_id = transfers.account_to) " +
                "join users on users.user_id = accounts.user_id " +
                "join transfer_statuses on transfer_statuses.transfer_status_id = transfers.transfer_status_id " +
                "join transfer_types on transfer_types.transfer_type_id = transfers.transfer_type_id " +
                "where users.user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while(results.next()) {
            TransactionDTO transactionDTO = mapRowToTransactionDTO(results);
            transactions.add(transactionDTO);
        }
        return transactions;
    }

    private TransactionDTO mapRowToTransactionDTO(SqlRowSet rs) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionId(rs.getLong("transfer_id"));
        transactionDTO.setTransactionType(rs.getString("transfer_type_desc"));
        transactionDTO.setTransactionStatus(rs.getString("transfer_status_desc"));
        transactionDTO.setFromAccountId(rs.getLong("account_from"));
        transactionDTO.setToAccountId(rs.getLong("account_to"));
        transactionDTO.setAmount(rs.getDouble("amount"));
        return transactionDTO;
    }
}
