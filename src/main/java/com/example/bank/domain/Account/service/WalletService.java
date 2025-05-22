package com.example.bank.domain.Account.service;

import com.example.bank.domain.Account.entity.Wallet;
import com.example.bank.domain.Account.entity.Bank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final Bank bank;
    private final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    public Wallet createAccount(Long id) throws InterruptedException {
        return bank.save(id);
    }

    public Wallet findById(Long id) throws InterruptedException {
        return bank.findById(id);
    }

    public Wallet credit(Long id, int amount) {
        checkAmount(amount);
        ReentrantLock lock = getReentrantLock(id);

        if (!lock.tryLock()) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT);
        }

        try {
            Wallet wallet = bank.findById(id);
            int newBalance = wallet.getBalance() + amount;
            return wallet.update(newBalance);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } finally {
            lock.unlock();
        }
    }

    public Wallet debit(Long id, int amount) {
        checkAmount(amount);
        ReentrantLock lock = getReentrantLock(id);

        try {
            Wallet wallet = bank.findById(id);
            if (wallet.getBalance() < amount) {
                throw new IllegalArgumentException("잔액 부족");
            }
            int newBalance = wallet.getBalance() - amount;
            return wallet.update(newBalance);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } finally {
            lock.unlock();
        }
    }

    private static void checkAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }

    private ReentrantLock getReentrantLock(Long id) {
        ReentrantLock lock = locks.computeIfAbsent(id, k -> new ReentrantLock());
        lock.lock();

        return lock;
    }

}
