package com.example.bank.domain.Account.controller;

import com.example.bank.domain.Account.entity.Wallet;
import com.example.bank.domain.Account.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/{id}")
    public ResponseEntity<Integer> createAccount(@PathVariable Long id) throws InterruptedException {
        return ResponseEntity.ok().body(walletService.createAccount(id).getBalance());
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<Wallet> getBalance(@PathVariable Long id) throws InterruptedException {
        return ResponseEntity.ok().body(walletService.findById(id));
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<String> credit(@PathVariable Long id, @RequestParam int amount) {
        return ResponseEntity.ok().body("현재 잔액: " + walletService.credit(id,amount).getBalance());
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<String> debit(@PathVariable Long id, @RequestParam int amount) {
        return ResponseEntity.ok().body("현재 잔액: " + walletService.debit(id,amount).getBalance());
    }
}
