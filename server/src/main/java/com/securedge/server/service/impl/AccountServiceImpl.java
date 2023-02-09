package com.securedge.server.service.impl;

import com.securedge.server.model.Account;
import com.securedge.server.repository.AccountRepository;
import com.securedge.server.service.AccountService;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        logger.info("Retrieving all accounts...");
        return accountRepository.findAll();
    }

    @Override
    public Account findById(int id) {
        logger.info("Find account by id: " + id);
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Account create(Account account) {
        logger.info("Validate create account request");
        if (StringUtil.isNullOrEmpty(account.getName())) {
            logger.info("Account name not provided");
            throw new RuntimeException("Account name not provided");
        }
        account.setId(null);
        account.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        logger.info("Creating new account for name: " + account.getName());
        return accountRepository.save(account);
    }

    @Override
    public Account update(Account account) {
        logger.info("Validate update account request");
        if (StringUtil.isNullOrEmpty(account.getName())) {
            logger.error("Account name not provided");
            throw new RuntimeException("Account name not provided");
        }
        if (Objects.isNull(account.getId())) {
            logger.error("Account id not provided");
            throw new RuntimeException("Account id not provided");
        }
        Optional<Account> accountOptional = accountRepository.findById(account.getId());
        if (accountOptional.isEmpty()) {
            logger.error("Unable to find account for id: " + account.getId());
            throw new RuntimeException("Unable to find account for id: " + account.getId());
        }

        Account dbAccount = accountOptional.get();
        dbAccount.setName(account.getName());
        logger.info("Updating account");
        return accountRepository.save(dbAccount);
    }

    @Override
    public void delete(int id) {

    }
}
