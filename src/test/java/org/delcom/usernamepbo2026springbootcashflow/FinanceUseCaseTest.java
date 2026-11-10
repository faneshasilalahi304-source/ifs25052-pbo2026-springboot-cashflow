package org.delcom.usernamepbo2026springbootcashflow;

import org.delcom.usernamepbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.usernamepbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.usernamepbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.usernamepbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.usernamepbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.usernamepbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.usernamepbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test use case + presenter tanpa Spring dan tanpa UI.
 */
class FinanceUseCaseTest {

    private FinanceUseCase useCase;
    private final FinancePresenter presenter = new FinancePresenter();

    @BeforeEach
    void setUp() {
        useCase = new FinanceUseCase(new TransactionRepository());
    }

    @Test
    void balanceIsIncomeMinusExpense() throws Exception {
        useCase.addIncome("Gaji", "5000000");
        useCase.addExpense("Makan", "25000");
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(useCase.getBalance()));
    }

    @Test
    void presenterFormatsTransactionLine() throws Exception {
        Transaction t = useCase.addIncome("Gaji", "1000000");
        assertEquals("[1] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.format(t));
    }

    @Test
    void sortOptionLabelHandlesNull() {
        assertEquals("(Tanpa urutan)", presenter.formatSortOption(null));
        assertEquals("Jumlah terkecil", presenter.formatSortOption(SortOption.AMOUNT_ASC));
    }

    @Test
    void invalidInputIsRejected() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("", "1000"));
        assertThrows(ValidationException.class, () -> useCase.addIncome("Gaji", "-5"));
        assertThrows(ValidationException.class, () -> useCase.addExpense("Makan", "0"));
        assertThrows(ValidationException.class, () -> useCase.addExpense("Makan", "abc"));
        assertThrows(ValidationException.class, () -> useCase.addExpense("Makan", ""));
    }

    @Test
    void removeUpdatesBalanceAndThrowsWhenMissing() throws Exception {
        useCase.addIncome("Gaji", "1000");
        Transaction expense = useCase.addExpense("Makan", "400");
        useCase.removeTransaction(expense.getId());
        assertEquals(0, new BigDecimal("1000").compareTo(useCase.getBalance()));
        assertThrows(EntityNotFoundException.class, () -> useCase.removeTransaction(99));
    }

    @Test
    void searchIsCaseInsensitiveAndDoesNotMutate() throws Exception {
        useCase.addIncome("Gaji Bulanan", "1000");
        useCase.addExpense("Makan Siang", "10");
        assertEquals(1, useCase.searchTransactions("gaji").size());
        assertEquals(2, useCase.getAllTransactions().size());
    }

    @Test
    void sortOptionsWork() throws Exception {
        useCase.addExpense("A", "300");
        useCase.addIncome("B", "100");
        useCase.addIncome("C", "200");
        List<Transaction> asc = useCase.sortTransactions(SortOption.AMOUNT_ASC);
        assertEquals("B", asc.get(0).getDescription());
        List<Transaction> desc = useCase.sortTransactions(SortOption.AMOUNT_DESC);
        assertEquals("A", desc.get(0).getDescription());
        assertTrue(useCase.sortTransactions(SortOption.INCOME_FIRST).get(0).isIncome());
        assertTrue(!useCase.sortTransactions(SortOption.EXPENSE_FIRST).get(0).isIncome());
    }
}
