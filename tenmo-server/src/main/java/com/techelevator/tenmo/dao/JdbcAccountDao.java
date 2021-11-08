package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //get account by user ID
    @Override
    public AccountDTO getAccount(Long id) {
        AccountDTO accountDTO = null;
        String sql = "SELECT * from accounts " +
                "join users on users.user_id = accounts.user_id " +
                "WHERE users.user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if(result.next()){
            accountDTO = mapRowToAccountDto(result);
        }
        return accountDTO;
    }

    private AccountDTO mapRowToAccountDto(SqlRowSet sqlRowSet){
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountId(sqlRowSet.getLong("account_id"));
        accountDTO.setUserId(sqlRowSet.getLong("user_id"));
        accountDTO.setBalance(sqlRowSet.getDouble("balance"));
        return accountDTO;

    }
}
