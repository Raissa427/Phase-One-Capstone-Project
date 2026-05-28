package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab2.dao.AccountDAO;
import com.igirepay.lab2.daoImpl.AcountDAOImpl;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import com.igirepay.lab2.db.DatabaseConnection;

/**
 * Screen for requesting and repaying loans.
 * Loan amounts are added to the wallet balance and tracked in loan_balance.
 */
public class LoanScreen {

    private final AccountService     accountService     = new AccountService();
    private final TransactionService transactionService = new TransactionService();
    private final AccountDAO         accountDAO         = new AcountDAOImpl();

    /** Builds and displays the loan management screen. */
    public void show(Stage stage, Customer customer) {
        // --- Title ---
        Label title = new Label("Loan Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #FFCC00;");

        // --- Current loan balance ---
        Label loanBalanceLabel = new Label("Current Loan Balance: " + loadLoanBalance(customer));
        loanBalanceLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 16px;");

        // --- Request loan section ---
        Label requestTitle = new Label("Request a Loan");
        requestTitle.setStyle("-fx-text-fill: #AAAAAA;");

        TextField requestField = new TextField();
        requestField.setPromptText("Loan Amount (RWF)");
        styleInput(requestField);

        Button requestBtn = new Button("Request Loan");
        styleButton(requestBtn, "#FFCC00", "#1A1A2E");

        // --- Repay loan section ---
        Label repayTitle = new Label("Repay Loan");
        repayTitle.setStyle("-fx-text-fill: #AAAAAA;");

        TextField repayField = new TextField();
        repayField.setPromptText("Repayment Amount (RWF)");
        styleInput(repayField);

        Button repayBtn = new Button("Repay Loan");
        styleButton(repayBtn, "#16213E", "#FFCC00");

        // --- Back button ---
        Button backButton = new Button("← Back");
        styleButton(backButton, "#16213E", "#FFCC00");

        // --- Layout ---
        VBox layout = new VBox(12, title, loanBalanceLabel,
                               requestTitle, requestField, requestBtn,
                               repayTitle, repayField, repayBtn,
                               backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Request loan action ---
        requestBtn.setOnAction(e -> {
            try {
                double amount   = Double.parseDouble(requestField.getText());
                int    walletId = getWalletAccountId(customer);

                // Add loan amount to wallet balance and record the loan
                transactionService.deposit(walletId, amount, UUID.randomUUID().toString());
                updateLoanBalance(walletId, amount, true);
                showAlert(Alert.AlertType.INFORMATION, "Loan Approved",
                        "Loan of " + String.format("RWF %.2f", amount) + " added to your wallet.");
                show(stage, customer);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid amount.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Loan Request Failed", ex.getMessage());
            }
        });

        // --- Repay loan action ---
        repayBtn.setOnAction(e -> {
            try {
                double amount   = Double.parseDouble(repayField.getText());
                int    walletId = getWalletAccountId(customer);

                // Deduct repayment from wallet and reduce loan balance
                transactionService.withdraw(walletId, amount, UUID.randomUUID().toString());
                updateLoanBalance(walletId, amount, false);
                showAlert(Alert.AlertType.INFORMATION, "Repayment Successful",
                        "Repaid " + String.format("RWF %.2f", amount) + " from your wallet.");
                show(stage, customer);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid amount.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Repayment Failed", ex.getMessage());
            }
        });

        backButton.setOnAction(e -> new DashboardScreen().show(stage, customer));

        stage.setScene(new Scene(layout, 420, 520));
        stage.setTitle("IgirePay — Loan");
        stage.show();
    }

    /** Returns the formatted loan balance for the customer's wallet account. */
    private String loadLoanBalance(Customer customer) {
        try {
            List<Account> accounts = accountService.getCustomerAccounts(customer.getId());
            for (Account account : accounts) {
                if ("WALLET".equals(account.getAccountType())) {
                    return String.format("RWF %.2f", account.getLoanBalance());
                }
            }
        } catch (Exception e) { /* fall through */ }
        return "RWF 0.00";
    }

    /** Finds and returns the wallet account ID for the given customer. */
    private int getWalletAccountId(Customer customer) throws Exception {
        List<Account> accounts = accountService.getCustomerAccounts(customer.getId());
        for (Account account : accounts) {
            if ("WALLET".equals(account.getAccountType())) {
                return account.getId();
            }
        }
        throw new Exception("No wallet account found.");
    }

    /**
     * Updates the loan_balance column in the accounts table.
     * If adding is true, increases the loan balance; otherwise decreases it.
     */
    private void updateLoanBalance(int accountId, double amount, boolean adding) throws Exception {
        String sql = adding
                ? "UPDATE accounts SET loan_balance = loan_balance + ? WHERE id = ?"
                : "UPDATE accounts SET loan_balance = GREATEST(0, loan_balance - ?) WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
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
