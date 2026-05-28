package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab1.model.Transaction;
import com.igirepay.lab2.service.AccountService;
import com.igirepay.lab2.service.CustomerService;
import com.igirepay.lab2.service.TransactionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * The admin management screen with three tabs:
 * Customers, Accounts, and Transactions.
 * Admins can lock/unlock customers and delete inactive accounts from here.
 */
public class AdminScreen {

    private final CustomerService    customerService    = new CustomerService();
    private final AccountService     accountService     = new AccountService();
    private final TransactionService transactionService = new TransactionService();

    /** Builds and displays the admin management screen. */
    public void show(Stage stage, Customer admin) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
                buildCustomersTab(stage, admin),
                buildAccountsTab(),
                buildTransactionsTab()
        );

        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: #1A1A2E; -fx-font-weight: bold;");
        backButton.setOnAction(e -> new AdminDashboardScreen().show(stage, admin));

        VBox layout = new VBox(10, backButton, tabPane);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        stage.setScene(new Scene(layout, 800, 550));
        stage.setTitle("IgirePay — Admin Management");
        stage.show();
    }

    /**
     * Builds the Customers tab with a table and lock/unlock action buttons.
     */
    private Tab buildCustomersTab(Stage stage, Customer admin) {
        TableView<Customer> table = new TableView<>();
        table.setStyle("-fx-background-color: #16213E; -fx-text-fill: #FFFFFF;");

        TableColumn<Customer, String> idCol     = new TableColumn<>("ID");
        TableColumn<Customer, String> nameCol   = new TableColumn<>("Name");
        TableColumn<Customer, String> phoneCol  = new TableColumn<>("Phone");
        TableColumn<Customer, String> roleCol   = new TableColumn<>("Role");
        TableColumn<Customer, String> lockedCol = new TableColumn<>("Locked");

        idCol.setCellValueFactory(c     -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        nameCol.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().getFullName()));
        phoneCol.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getPhoneNumber()));
        roleCol.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().getRole()));
        lockedCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isLocked() ? "Yes" : "No"));

        table.getColumns().addAll(idCol, nameCol, phoneCol, roleCol, lockedCol);
        loadCustomers(table);

        // --- Action buttons ---
        Button lockBtn   = new Button("Lock Account");
        Button unlockBtn = new Button("Unlock Account");
        styleActionButton(lockBtn,   "#FF4444");
        styleActionButton(unlockBtn, "#00C851");

        lockBtn.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Please select a customer."); return; }
            try {
                customerService.setAccountLocked(selected.getId(), true);
                showInfo("Account locked successfully.");
                loadCustomers(table);
            } catch (Exception ex) { showAlert(ex.getMessage()); }
        });

        unlockBtn.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Please select a customer."); return; }
            try {
                customerService.setAccountLocked(selected.getId(), false);
                showInfo("Account unlocked successfully.");
                loadCustomers(table);
            } catch (Exception ex) { showAlert(ex.getMessage()); }
        });

        HBox actions = new HBox(10, lockBtn, unlockBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(10, table, actions);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: #1A1A2E;");

        Tab tab = new Tab("Customers", content);
        return tab;
    }

    /**
     * Builds the Accounts tab with a table and a delete inactive account button.
     */
    private Tab buildAccountsTab() {
        TableView<Account> table = new TableView<>();

        TableColumn<Account, String> idCol      = new TableColumn<>("ID");
        TableColumn<Account, String> custIdCol  = new TableColumn<>("Customer ID");
        TableColumn<Account, String> typeCol    = new TableColumn<>("Type");
        TableColumn<Account, String> balanceCol = new TableColumn<>("Balance");
        TableColumn<Account, String> activeCol  = new TableColumn<>("Active");

        idCol.setCellValueFactory(c      -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        custIdCol.setCellValueFactory(c  -> new SimpleStringProperty(String.valueOf(c.getValue().getCustomerId())));
        typeCol.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getAccountType()));
        balanceCol.setCellValueFactory(c -> new SimpleStringProperty(String.format("RWF %.2f", c.getValue().getBalance())));
        activeCol.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().isActive() ? "Yes" : "No"));

        table.getColumns().addAll(idCol, custIdCol, typeCol, balanceCol, activeCol);
        loadAccounts(table);

        Button deleteBtn = new Button("Delete Inactive Account");
        styleActionButton(deleteBtn, "#FF4444");

        deleteBtn.setOnAction(e -> {
            Account selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Please select an account."); return; }
            try {
                accountService.deleteInactiveAccount(selected.getId());
                showInfo("Account deleted.");
                loadAccounts(table);
            } catch (Exception ex) { showAlert(ex.getMessage()); }
        });

        VBox content = new VBox(10, table, deleteBtn);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: #1A1A2E;");

        return new Tab("Accounts", content);
    }

    /**
     * Builds the Transactions tab showing all transactions system-wide.
     */
    private Tab buildTransactionsTab() {
        TableView<Transaction> table = new TableView<>();

        TableColumn<Transaction, String> idCol    = new TableColumn<>("ID");
        TableColumn<Transaction, String> refCol   = new TableColumn<>("Reference ID");
        TableColumn<Transaction, String> typeCol  = new TableColumn<>("Type");
        TableColumn<Transaction, String> amtCol   = new TableColumn<>("Amount");
        TableColumn<Transaction, String> dateCol  = new TableColumn<>("Date");

        idCol.setCellValueFactory(c   -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        refCol.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getReferenceId()));
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTransactionType()));
        amtCol.setCellValueFactory(c  -> new SimpleStringProperty(String.format("RWF %.2f", c.getValue().getAmount())));
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt().toString()));

        table.getColumns().addAll(idCol, refCol, typeCol, amtCol, dateCol);

        try {
            List<Transaction> all = transactionService.getAllTransactions();
            table.setItems(FXCollections.observableArrayList(all));
        } catch (Exception e) {
            showAlert("Could not load transactions: " + e.getMessage());
        }

        VBox content = new VBox(10, table);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: #1A1A2E;");

        return new Tab("Transactions", content);
    }

    /** Loads all customers into the given table. */
    private void loadCustomers(TableView<Customer> table) {
        try {
            table.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
        } catch (Exception e) { showAlert("Could not load customers: " + e.getMessage()); }
    }

    /** Loads all accounts into the given table. */
    private void loadAccounts(TableView<Account> table) {
        try {
            table.setItems(FXCollections.observableArrayList(accountService.getAllAccounts()));
        } catch (Exception e) { showAlert("Could not load accounts: " + e.getMessage()); }
    }

    /** Applies a colored style to an action button. */
    private void styleActionButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #FFFFFF; " +
                        "-fx-font-weight: bold; -fx-background-radius: 5;");
    }

    /** Shows an error alert. */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Shows an info alert. */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
