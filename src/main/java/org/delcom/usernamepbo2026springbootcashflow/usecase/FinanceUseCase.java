package org.delcom.usernamepbo2026springbootcashflow.usecase;

import org.delcom.usernamepbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.usernamepbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.usernamepbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.usernamepbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.usernamepbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.usernamepbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Use case yang menangani logika bisnis aplikasi cashflow.
 * Tidak melakukan I/O UI; hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class FinanceUseCase {
    /** Panjang maksimum teks jumlah yang diterima. */
    private static final int MAX_AMOUNT_LENGTH = 18;

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;

    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Mengambil semua transaksi. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /** Menambahkan pemasukan setelah validasi keterangan dan jumlah. */
    public Transaction addIncome(String description, String amountText) throws ValidationException {
        String cleanDescription = validateDescription(description);
        BigDecimal amount = parseAmount(amountText);
        return transactionRepository.saveIncome(cleanDescription, amount);
    }

    /** Menambahkan pengeluaran setelah validasi keterangan dan jumlah. */
    public Transaction addExpense(String description, String amountText) throws ValidationException {
        String cleanDescription = validateDescription(description);
        BigDecimal amount = parseAmount(amountText);
        return transactionRepository.saveExpense(cleanDescription, amount);
    }

    /** Menghapus transaksi berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeTransaction(int id) throws EntityNotFoundException {
        boolean deleted = transactionRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        return transactionRepository.findBy(
                transaction -> transaction.getDescription().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan transaksi sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Transaction> sortTransactions(SortOption option) {
        return transactionRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Total seluruh pemasukan. */
    public BigDecimal getTotalIncome() {
        return totalOf(TransactionType.INCOME);
    }

    /** Total seluruh pengeluaran. */
    public BigDecimal getTotalExpense() {
        return totalOf(TransactionType.EXPENSE);
    }

    /** Saldo saat ini = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    private BigDecimal totalOf(TransactionType type) {
        return transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String validateDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        return description.trim();
    }

    /** Mengubah teks jumlah menjadi BigDecimal positif dengan 2 digit desimal. */
    private BigDecimal parseAmount(String amountText) throws ValidationException {
        if (amountText == null || amountText.trim().isEmpty()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        String text = amountText.trim();
        if (!text.matches("-?\\d+(\\.\\d+)?")) {
            throw new ValidationException("Jumlah harus berupa angka (contoh: 150000 atau 150000.50)");
        }
        if (text.length() > MAX_AMOUNT_LENGTH) {
            throw new ValidationException("Jumlah terlalu besar");
        }
        BigDecimal amount = new BigDecimal(text);
        if (amount.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari 0");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
