package com.example.bank.domain.Account.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Wallet {

    private int balance;
    private LocalDateTime updateTime;

    public Wallet update(int balance) {
        this.balance = balance;
        this.updateTime = LocalDateTime.now();
        return this;
    }
}
