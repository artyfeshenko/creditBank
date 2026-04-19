package ru.feshenko.credit.bank.deal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.feshenko.credit.bank.deal.dto.LoanOfferDto;
import ru.feshenko.credit.bank.deal.entity.Statement;
import ru.feshenko.credit.bank.deal.repository.StatementRepository;
import ru.feshenko.credit.bank.deal.service.DealService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DealServiceLockTest {
    @Autowired
    private DealService dealService;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Statement createStatement() {
        Statement statement = new Statement();
        statement.setStatusHistory(new ArrayList<>());
        return statement;
    }

    private LoanOfferDto createOffer(UUID id) {
        return new LoanOfferDto(
                id,
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(100000),
                12,
                BigDecimal.valueOf(9263.45),
                BigDecimal.valueOf(20),
                false,
                false
        );
    }

    @Test
    void shouldWaitForLockWhenConcurrentAccess() throws Exception {
        Statement statement = statementRepository.save(createStatement());
        LoanOfferDto offer = createOffer(statement.getStatementId());

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch lockAcquiredLatch = new CountDownLatch(1);
        CountDownLatch releaseLatch = new CountDownLatch(1);

        executor.submit(() -> {
            TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

            txTemplate.execute(status -> {
                statementRepository.findByIdForUpdate(statement.getStatementId());

                lockAcquiredLatch.countDown();

                try {
                    releaseLatch.await();
                } catch (InterruptedException ignored) {}

                return null;
            });
        });

        lockAcquiredLatch.await();

        Future<Long> second = executor.submit(() -> {
            long start = System.currentTimeMillis();

            dealService.selectLoanOffer(offer);

            return System.currentTimeMillis() - start;
        });

        Thread.sleep(500);

        releaseLatch.countDown();

        long duration = second.get();

        executor.shutdown();

        assertTrue(duration >= 400);
    }
}

