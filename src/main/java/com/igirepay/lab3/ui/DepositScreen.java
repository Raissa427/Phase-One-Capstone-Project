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
 * Screen for depositing money into the customer's wallet account.
 * A unique reference ID is auto-generated for each deposit.
 */
public class DepositScreen {

    private final TransactionService transactionService = new TransactionService();
    private final AccountService     accountService     = new AccountService();

    /** Builds and displays the deposit screen. */
    public void show(Stage stage, Customer customer) {
        // --- Title ---
        Label title = new Label("Deposit Money");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #FFCC00;");

        // --- Amount input ---
        TextField amountField = new TextField();
        amountField.setPromptText("Amount (RWF)");
        styleInput(amountField);

        // --- Auto-generated reference ID (read-only) ---
        Label refLabel = new Label("Reference ID:");
        refLabel.setStyle("-fx-text-fill: #AAAAAA;");

        Label refIdLabel = new Label(UUID.randomUUID().toString());
        refIdLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 11px;");

        // --- Buttons ---
        Button depositButton = new Button("Deposit");
        styleButton(depositButton, "#FFCC00", "#1A1A2E");

        Button backButton = new Button("← Back");
        styleButton(backButton, "#16213E", "#FFCC00");

        // --- Layout ---
        VBox layout = new VBox(15, title, amountField, refLabel, refIdLabel, depositButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Deposit button action ---
        depositButton.setOnAction(e -> {
            try {
                double amount      = Double.parseDouble(amountField.getText());
                int    walletId    = getWalletAccountId(customer);
                String referenceId = refIdLabel.getText();

                transactionService.deposit(walletId, amount, referenceId);
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Deposited " + String.format("RWF %.2f", amount) + " successfully.");
                new DashboardScreen().show(stage, customer);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid amount.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Deposit Failed", ex.getMessage());
            }
        });

        backButton.setOnAction(e -> new DashboardScreen().show(stage, customer));

        stage.setScene(new Scene(layout, 420, 380));
        stage.setTitle("IgirePay — Deposit");
        stage.show();
    }

    /**
     * Finds and returns the wallet account ID for the given customer.
     * Throws an exception if no wallet account is found.
     */
    private int getWalletAccountId(Customer customer) throws Exception {
        List<Account> accounts = accountService.getCustomerAccounts(customer.getId());
        for (Account account : accounts) {
            if ("WALLET".equals(account.getAccountType())) {
                return account.getId();
            }
        }
        throw new Exception("No wallet account found.");
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
