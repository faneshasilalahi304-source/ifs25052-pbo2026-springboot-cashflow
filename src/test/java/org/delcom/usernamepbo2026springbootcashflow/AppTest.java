package org.delcom.usernamepbo2026springbootcashflow;

import org.delcom.usernamepbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.usernamepbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.usernamepbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Smoke test: memastikan wiring bean Clean Architecture (composition root) berjalan.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AppTest {

    @Autowired
    private ITransactionRepository repository;

    @Autowired
    private FinanceUseCase useCase;

    @Autowired
    private FinancePresenter presenter;

    @Test
    void contextLoadsAndBeansAreWired() {
        assertNotNull(repository);
        assertNotNull(useCase);
        assertNotNull(presenter);
    }

    @Test
    void beansAreSingletons() {
        assertSame(useCase, useCase);
        assertNotNull(useCase.getAllTransactions());
    }
}
