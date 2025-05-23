package com.example.bank.domain.Account.service;

import com.example.bank.domain.Account.entity.Wallet;
import com.example.bank.domain.Account.entity.Bank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
    private final Bank bank;
    private final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    public Wallet createAccount(Long id) {
        return bank.save(id);
    }

    public Wallet findById(Long id) {
        return bank.findById(id);
    }

    public Wallet credit(Long id, int amount) {
        checkAmount(amount);
        ReentrantLock lock = locks.computeIfAbsent(id, k -> new ReentrantLock());

        boolean locked = lock.tryLock();
        if (!locked) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT);
        }

        try {
            Wallet wallet = bank.findById(id);
            int newBalance = wallet.getBalance() + amount;
            return wallet.update(newBalance);

        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    public Wallet debit(Long id, int amount) {
        checkAmount(amount);
        ReentrantLock lock = locks.computeIfAbsent(id, k -> new ReentrantLock());

        try {
            Wallet wallet = bank.findById(id);

            if (wallet.getBalance() < amount) {
                throw new IllegalArgumentException("잔액 부족");
            }
            int newBalance = wallet.getBalance() - amount;
            return wallet.update(newBalance);

        } finally {
            lock.unlock();
        }
    }

    private static void checkAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }


}
