package com.example.bank;

import com.example.bank.domain.Account.entity.Bank;
import com.example.bank.domain.Account.entity.Wallet;
import com.example.bank.domain.Account.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BankApplicationTests {

    @Autowired
    WalletService walletService;

    @Autowired
    Bank bank;

    Logger log = LoggerFactory.getLogger(this.getClass());

    @BeforeEach
    void setUp() {
        bank.deleteAll();
    }

    @Test
    void contextLoads() throws InterruptedException {
        Long id = 1L;
        Wallet newWallet = walletService.createAccount(id);

        assertEquals(newWallet.getBalance(), 0);
        log.info("1-pass");

        assertEquals(walletService.credit(1L,10000).getBalance(), 10000);
        log.info("2-pass");

        assertEquals(walletService.debit(1L,5000).getBalance(), 5000);
        log.info("서비스 로직 정상 동작 테스트 완료");
    }

    @Test
    void 한명에게_동시에_입금요청이_올_경우_실패_테스트() {
        log.info("동시 입금 요청 실페 테스트 시작");

    }

}
