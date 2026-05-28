package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab2.service.AccountService;
import com.igirepay.lab2.service.TransactionService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.UUID;

/**
 * Screen for managing savings.
 * Allows the customer to transfer money between their wallet and savings accounts.
 */
public class SavingsScreen {

    private final AccountService     accountService     = new AccountService();
    private final TransactionService transactionService = new TransactionService();

    /** Builds and displays the savings management screen. */
    public void show(Stage stage, Customer customer) {
        // --- Title ---
        Label title = new Label("Savings Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #FFCC00;");

        // --- Savings balance display ---
        Label savingsBalanceLabel = new Label("Savings Balance: " + loadSavingsBalance(customer));
        savingsBalanceLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 16px;");

        // --- Transfer Wallet → Savings ---
        Label toSavingsTitle = new Label("Transfer Wallet → Savings");
        toSavingsTitle.setStyle("-fx-text-fill: #AAAAAA;");

        TextField toSavingsField = new TextField();
        toSavingsField.setPromptText("Amount (RWF)");
        styleInput(toSavingsField);

        Button toSavingsBtn = new Button("Transfer to Savings");
        styleButton(toSavingsBtn, "#FFCC00", "#1A1A2E");

        // --- Transfer Savings → Wallet ---
        Label toWalletTitle = new Label("Transfer Savings → Wallet");
        toWalletTitle.setStyle("-fx-text-fill: #AAAAAA;");

        TextField toWalletField = new TextField();
        toWalletField.setPromptText("Amount (RWF)");
        styleInput(toWalletField);

        Button toWalletBtn = new Button("Transfer to Wallet");
        styleButton(toWalletBtn, "#16213E", "#FFCC00");

        // --- Back button ---
        Button backButton = new Button("← Back");
        styleButton(backButton, "#16213E", "#FFCC00");

        // --- Layout ---
        VBox layout = new VBox(12, title, savingsBalanceLabel,
                               toSavingsTitle, toSavingsField, toSavingsBtn,
                               toWalletTitle, toWalletField, toWalletBtn,
                               backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Transfer Wallet → Savings action ---
        toSavingsBtn.setOnAction(e -> {
            try {
                double amount     = Double.parseDouble(toSavingsField.getText());
                int    walletId   = getAccountId(customer, "WALLET");
                int    savingsId  = getOrCreateSavingsId(customer);

                // Withdraw from wallet, deposit into savings
                transactionService.withdraw(walletId,  amount, UUID.randomUUID().toString());
                transactionService.deposit(savingsId,  amount, UUID.randomUUID().toString());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Transferred to savings.");
                show(stage, customer); // refresh screen
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid amount.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Transfer Failed", ex.getMessage());
            }
        });

        // --- Transfer Savings → Wallet action ---
        toWalletBtn.setOnAction(e -> {
            try {
                double amount    = Double.parseDouble(toWalletField.getText());
                int    savingsId = getAccountId(customer, "SAVINGS");
                int    walletId  = getAccountId(customer, "WALLET");

                // Withdraw from savings (applies 2% fee), deposit into wallet
                transactionService.withdraw(savingsId, amount, UUID.randomUUID().toString());
                transactionService.deposit(walletId,   amount, UUID.randomUUID().toString());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Transferred to wallet.");
                show(stage, customer); // refresh screen
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid amount.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Transfer Failed", ex.getMessage());
            }
        });

        backButton.setOnAction(e -> new DashboardScreen().show(stage, customer));

        stage.setScene(new Scene(layout, 420, 520));
        stage.setTitle("IgirePay — Savings");
        stage.show();
    }

    /** Returns the formatted savings balance, or 0.00 if no savings account exists. */
    private String loadSavingsBalance(Customer customer) {
        try {
            List<Account> accounts = accountService.getCustomerAccounts(customer.getId());
            for (Account account : accounts) {
                if ("SAVINGS".equals(account.getAccountType())) {
                    return String.format("RWF %.2f", account.getBalance());
                }
            }
        } catch (Exception e) { /* fall through */ }
        return "RWF 0.00";
    }

    /** Finds the account ID for the given type, or throws if not found. */
    private int getAccountId(Customer customer, String type) throws Exception {
        List<Account> accounts = accountService.getCustomerAccounts(customer.getId());
        for (Account account : accounts) {
            if (type.equals(account.getAccountType())) {
                return account.getId();
            }
        }
        throw new Exception("No " + type + " account found.");
    }

    /** Returns the savings account ID, creating one if it does not exist yet. */
    private int getOrCreateSavingsId(Customer customer) throws Exception {
        try {
            return getAccountId(customer, "SAVINGS");
        } catch (Exception e) {
            Account savings = accountService.createSavingsAccount(customer.getId());
            return savings.getId();
        }
    }

    private void styleInput(TextField field) {
        field.setMaxWidth(300);
        field.setStyle("-fx-background-color: #16213E; -fx-text-fill: #FFFFFF; " +
                       "-fx-prompt-text-fill: #888888; -fx-border-color: #FFCC00; " +
                       "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8;");
    }

    private void styleButton(Button button, String bg, String fg) {
        button.setMaxWidth(300);
        button.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; " +
                        "-fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10;");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
