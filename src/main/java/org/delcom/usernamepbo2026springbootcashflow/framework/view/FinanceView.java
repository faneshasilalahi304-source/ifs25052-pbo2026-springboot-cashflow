package org.delcom.usernamepbo2026springbootcashflow.framework.view;

import org.delcom.usernamepbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.usernamepbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.usernamepbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.usernamepbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.usernamepbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.usernamepbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.usernamepbo2026springbootcashflow.usecase.FinanceUseCase;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Tampilan web Vaadin aplikasi cashflow.
 * Pengurutan bisa lewat klik header kolom atau lewat pilihan SortOption domain.
 */
@Route("")
@PageTitle("Cashflow")
public class FinanceView extends VerticalLayout {

    private final FinanceUseCase financeUseCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final Select<SortOption> sortSelect = new Select<>();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span("- Data transaksi belum tersedia!");

    private final Span totalCount = new Span("0");
    private final Span incomeTotal = new Span("Rp 0.00");
    private final Span expenseTotal = new Span("Rp 0.00");
    private final Span balanceValue = new Span("Rp 0.00");

    public FinanceView(FinanceUseCase financeUseCase, FinancePresenter presenter) {
        this.financeUseCase = financeUseCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Cashflow - Catatan Keuangan");
        configureGrid();
        configureToolbar();

        add(title, buildSummaryCards(), buildToolbar(), statusLabel, grid);
        refreshAll();
    }

    // ------------------------------------------------------------------ ringkasan

    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout summary = new HorizontalLayout(
                createSummaryCard("Jumlah Transaksi", totalCount, "#DBEAFE", "#1D4ED8", "#1E40AF"),
                createSummaryCard("Total Pemasukan", incomeTotal, "#DCFCE7", "#16A34A", "#166534"),
                createSummaryCard("Total Pengeluaran", expenseTotal, "#FEE2E2", "#DC2626", "#991B1B"),
                createSummaryCard("Saldo", balanceValue, "#FEF3C7", "#D97706", "#92400E")
        );
        summary.setWidthFull();
        summary.setSpacing(true);
        summary.getStyle().set("align-items", "stretch");
        summary.getStyle().set("flex-wrap", "wrap");
        return summary;
    }

    private Div createSummaryCard(String label, Span value, String background, String accent,
                                  String valueColor) {
        Span caption = new Span(label);
        caption.getStyle()
                .set("display", "block")
                .set("font-size", "0.875rem")
                .set("font-weight", "500")
                .set("color", accent);

        value.getStyle()
                .set("display", "block")
                .set("margin-top", "0.35rem")
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("line-height", "1.1")
                .set("color", valueColor);

        Div card = new Div(caption, value);
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "200px")
                .set("background", background)
                .set("border", "1px solid " + accent)
                .set("border-left", "6px solid " + accent)
                .set("border-radius", "12px")
                .set("padding", "1rem 1.1rem")
                .set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        List<Transaction> all = financeUseCase.getAllTransactions();
        totalCount.setText(String.valueOf(all.size()));
        incomeTotal.setText(presenter.formatMoney(financeUseCase.getTotalIncome()));
        expenseTotal.setText(presenter.formatMoney(financeUseCase.getTotalExpense()));
        balanceValue.setText(presenter.formatMoney(financeUseCase.getBalance()));
    }

    // ------------------------------------------------------------------ grid

    private void configureGrid() {
        grid.addColumn(Transaction::getId)
                .setHeader("ID")
                .setKey("id")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getId));

        grid.addColumn(Transaction::getDescription)
                .setHeader("Keterangan")
                .setKey("description")
                .setFlexGrow(1)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription,
                        String.CASE_INSENSITIVE_ORDER));

        grid.addColumn(transaction -> presenter.formatMoney(transaction.getAmount()))
                .setHeader("Jumlah")
                .setKey("amount")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getAmount));

        grid.addComponentColumn(this::createTypeBadge)
                .setHeader("Tipe")
                .setKey("type")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getType));

        grid.addColumn(transaction -> presenter.formatCreatedAt(transaction))
                .setHeader("Waktu")
                .setKey("createdAt")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getCreatedAt));

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Tindakan")
                .setKey("tindakan")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(false);

        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private Span createTypeBadge(Transaction transaction) {
        Span badge = new Span(presenter.formatType(transaction.getType()));
        String color = transaction.isIncome() ? "#16A34A" : "#DC2626";
        badge.getStyle()
                .set("color", color)
                .set("font-weight", "600");
        return badge;
    }

    private HorizontalLayout createActionButtons(Transaction transaction) {
        Button deleteButton = new Button("Hapus", VaadinIcon.TRASH.create(),
                event -> openDeleteDialog(transaction));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return new HorizontalLayout(deleteButton);
    }

    // ------------------------------------------------------------------ toolbar

    private void configureToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);

        sortSelect.setPlaceholder("Urutkan...");
        sortSelect.setItems(SortOption.values());
        sortSelect.setItemLabelGenerator(presenter::formatSortOption);
        sortSelect.setEmptySelectionAllowed(true);
        sortSelect.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                applySort(event.getValue());
            }
        });
    }

    private HorizontalLayout buildToolbar() {
        Button incomeButton = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(),
                event -> openAddDialog(TransactionType.INCOME));
        incomeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button expenseButton = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(),
                event -> openAddDialog(TransactionType.EXPENSE));
        expenseButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button searchButton = new Button("Cari", VaadinIcon.SEARCH.create(), event -> searchTransactions());
        Button refreshButton = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), event -> refreshAll());

        HorizontalLayout toolbar = new HorizontalLayout(
                searchField,
                searchButton,
                sortSelect,
                refreshButton,
                incomeButton,
                expenseButton
        );
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        toolbar.getStyle().set("flex-wrap", "wrap");
        return toolbar;
    }

    // ------------------------------------------------------------------ dialog

    private void openAddDialog(TransactionType type) {
        final boolean income = type == TransactionType.INCOME;

        TextField descriptionField = new TextField("Keterangan");
        descriptionField.setWidthFull();

        TextField amountField = new TextField("Jumlah");
        amountField.setPrefixComponent(new Span("Rp"));
        amountField.setPlaceholder("contoh: 150000");
        amountField.setWidthFull();

        VerticalLayout form = new VerticalLayout(descriptionField, amountField);
        form.setPadding(false);

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(income ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.setWidth("480px");
        dialog.setMaxWidth("95vw");
        dialog.add(form);

        Button saveButton = new Button("Simpan", VaadinIcon.CHECK.create(), event -> {
            try {
                Transaction created = income
                        ? financeUseCase.addIncome(descriptionField.getValue(), amountField.getValue())
                        : financeUseCase.addExpense(descriptionField.getValue(), amountField.getValue());
                showSuccess(presenter.showAddSuccess(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(), event -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
        descriptionField.focus();
    }

    private void openDeleteDialog(Transaction transaction) {
        Paragraph message = new Paragraph(
                "Yakin ingin menghapus transaksi \"" + transaction.getDescription()
                        + "\" (ID " + transaction.getId() + ")?"
        );
        message.getStyle().set("margin", "0");

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Menghapus Transaksi");
        dialog.setWidth("480px");
        dialog.setMaxWidth("95vw");
        dialog.add(message);

        Button confirmButton = new Button("Hapus", VaadinIcon.TRASH.create(), event -> {
            deleteTransaction(transaction);
            dialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(), event -> dialog.close());

        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }

    // ------------------------------------------------------------------ aksi

    private void deleteTransaction(Transaction transaction) {
        try {
            financeUseCase.removeTransaction(transaction.getId());
            showSuccess(presenter.showRemoveSuccess());
            refreshAll();
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
            refreshAll();
        }
    }

    private void searchTransactions() {
        String keyword = searchField.getValue();
        if (keyword == null || keyword.isBlank()) {
            refreshAll();
            return;
        }
        List<Transaction> results = financeUseCase.searchTransactions(keyword);
        emptyState.setText(results.isEmpty()
                ? "- Transaksi tidak ditemukan!"
                : "- Data transaksi belum tersedia!");
        grid.sort(Collections.emptyList());
        refreshGrid(results, "Hasil Pencarian: \"" + keyword + "\"");
    }

    private void applySort(SortOption option) {
        emptyState.setText("- Data transaksi belum tersedia!");
        searchField.clear();
        grid.sort(Collections.emptyList());
        refreshGrid(financeUseCase.sortTransactions(option),
                "Daftar Transaksi (" + presenter.formatSortOption(option) + ")");
    }

    private void refreshAll() {
        emptyState.setText("- Data transaksi belum tersedia!");
        searchField.clear();
        sortSelect.setValue(null);
        refreshGrid(financeUseCase.getAllTransactions(), "Daftar Transaksi");
    }

    private void refreshGrid(List<Transaction> transactions, String title) {
        grid.setItems(transactions);
        updateSummary();
        if (transactions.isEmpty()) {
            statusLabel.setText(title + " - " + emptyState.getText().replaceFirst("^- ", ""));
        } else {
            statusLabel.setText(title + " | " + presenter.formatBalance(financeUseCase.getBalance()));
        }
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
