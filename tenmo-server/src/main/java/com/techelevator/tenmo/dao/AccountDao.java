package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountDTO;

public interface AccountDao {

    AccountDTO getAccountBalance(Long id);
}
