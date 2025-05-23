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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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
    void 한명에게_동시에_입금요청이_올_경우_실패_테스트() throws InterruptedException {
        log.info("동시 입금 요청 실패 테스트 시작");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        Long id = 1L;
        int creditAmount = 1000;
        int cnt = 10;
        CountDownLatch countDownLatch = new CountDownLatch(cnt);
        Wallet newWallet = walletService.createAccount(id);

        for (int i = 0; i < cnt; i++) {
            executor.submit(() -> {
                try {
                    walletService.credit(id, creditAmount);
                } catch (Exception e) {
                    exceptions.add(e);
                    log.info("입금 실패: {}", e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executor.shutdown();

        for (Throwable e : exceptions) {
            assertInstanceOf(HttpClientErrorException.class, e);
            assertEquals(((HttpClientErrorException) e).getStatusCode(), HttpStatus.CONFLICT);
        }

        log.info("동시 입금 실패 테스트 완료 / 현재 잔액: {}", newWallet.getBalance());
    }

}
