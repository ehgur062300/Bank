package com.example.bank.domain.Account.entity;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

import static java.lang.Math.random;

@Component
public class Bank {

    HashMap<Long, Wallet> bank = new HashMap<>();

    public Wallet save(Long id) {
        Wallet newWallet = new Wallet(0, LocalDateTime.now());
        bank.put(id, newWallet);
        return newWallet;
    }

    public Wallet findById(Long id) {
        return bank.getOrDefault(id, null);
    }

    public void deleteAll() {
        bank.clear();
    }
}
