package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab1.model.Transaction;
import com.igirepay.lab2.service.AccountService;
import com.igirepay.lab2.service.TransactionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen showing the transaction history for the logged-in customer.
 * Admins see all transactions; users see only their own.
 * Includes an Export to CSV button.
 */
public class HistoryScreen {

    private final TransactionService transactionService = new TransactionService();
    private final AccountService     accountService     = new AccountService();

    /** Builds and displays the history screen. */
    public void show(Stage stage, Customer customer) {
        // --- Title ---
        Label title = new Label("Transaction History");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #FFCC00;");

        // --- Table ---
        TableView<Transaction> table = new TableView<>();
        table.setStyle("-fx-background-color: #16213E;");

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

        refCol.setPrefWidth(220);
        table.getColumns().addAll(idCol, refCol, typeCol, amtCol, dateCol);

        // Load transactions based on role
        loadTransactions(table, customer);

        // --- Export and Back buttons ---
        Button exportBtn = new Button("Export to CSV");
        exportBtn.setStyle("-fx-background-color: #00C851; -fx-text-fill: #FFFFFF; " +
                           "-fx-font-weight: bold; -fx-background-radius: 5;");

        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: #16213E; -fx-text-fill: #FFCC00; " +
                            "-fx-font-weight: bold; -fx-border-color: #FFCC00; -fx-background-radius: 5;");

        HBox buttons = new HBox(10, exportBtn, backButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        // --- Layout ---
        VBox layout = new VBox(15, title, table, buttons);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Export button action ---
        exportBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CSV File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    int walletId = getFirstAccountId(customer);
                    transactionService.exportToCSV(walletId, file.getAbsolutePath());
                    showAlert(Alert.AlertType.INFORMATION, "Exported", "History saved to " + file.getName());
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Export Failed", ex.getMessage());
                }
            }
        });

        // Back button routes to the correct dashboard based on role
        backButton.setOnAction(e -> {
            if ("ADMIN".equals(customer.getRole())) {
                new AdminDashboardScreen().show(stage, customer);
            } else {
                new DashboardScreen().show(stage, customer);
            }
        });

        stage.setScene(new Scene(layout, 780, 500));
        stage.setTitle("IgirePay — History");
        stage.show();
    }

    /**
     * Loads transactions into the table.
     * Admins see all transactions; users see only their own accounts' transactions.
     */
    private void loadTransactions(TableView<Transaction> table, Customer customer) {
        try {
            List<Transaction> transactions;

            if ("ADMIN".equals(customer.getRole())) {
                transactions = transactionService.getAllTransactions();
            } else {
                transactions = new ArrayList<>();
                List<Account> accounts = accountService.getCustomerAccounts(customer.getId());
                for (Account account : accounts) {
                    transactions.addAll(transactionService.getTransactionHistory(account.getId()));
                }
            }

            table.setItems(FXCollections.observableArrayList(transactions));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load transactions: " + e.getMessage());
        }
    }

    /** Returns the ID of the first account found for the customer. */
    private int getFirstAccountId(Customer customer) throws Exception {
        List<Account> accounts = accountService.getCustomerAccounts(customer.getId());
        if (accounts.isEmpty()) throw new Exception("No accounts found.");
        return accounts.get(0).getId();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
