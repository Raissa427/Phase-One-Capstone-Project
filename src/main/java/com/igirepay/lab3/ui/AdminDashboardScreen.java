package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Customer;
import com.igirepay.lab2.service.AccountService;
import com.igirepay.lab2.service.CustomerService;
import com.igirepay.lab2.service.TransactionService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * The main dashboard for ADMIN users.
 * Shows system-wide stats and navigation to admin management screens.
 */
public class AdminDashboardScreen {

    private final CustomerService    customerService    = new CustomerService();
    private final AccountService     accountService     = new AccountService();
    private final TransactionService transactionService = new TransactionService();

    /** Builds and displays the admin dashboard on the given stage. */
    public void show(Stage stage, Customer admin) {
        // --- Title ---
        Label title = new Label("Admin Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #FFCC00;");

        Label welcomeLabel = new Label("Welcome, " + admin.getFullName());
        welcomeLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px;");

        // --- Stats cards ---
        HBox statsRow = new HBox(15,
                buildStatCard("Customers",    countCustomers()),
                buildStatCard("Accounts",     countAccounts()),
                buildStatCard("Transactions", countTransactions())
        );
        statsRow.setAlignment(Pos.CENTER);

        // --- Navigation buttons ---
        Button customersBtn    = createNavButton("Manage Customers");
        Button allTxBtn        = createNavButton("All Transactions");
        Button logoutBtn       = createNavButton("Logout");

        VBox buttons = new VBox(12, customersBtn, allTxBtn, logoutBtn);
        buttons.setAlignment(Pos.CENTER);

        // --- Main layout ---
        VBox layout = new VBox(20, title, welcomeLabel, statsRow, buttons);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Button actions ---
        customersBtn.setOnAction(e -> new AdminScreen().show(stage, admin));
        allTxBtn.setOnAction(e     -> new HistoryScreen().show(stage, admin));
        logoutBtn.setOnAction(e    -> new LoginScreen().show(stage));

        stage.setScene(new Scene(layout, 500, 500));
        stage.setTitle("IgirePay — Admin Dashboard");
        stage.show();
    }

    /**
     * Builds a small stat card showing a label and a count value.
     */
    private VBox buildStatCard(String label, String value) {
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: #FFCC00;");

        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 12px;");

        VBox card = new VBox(5, valueLabel, nameLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(130);
        card.setStyle("-fx-background-color: #16213E; -fx-background-radius: 8;");
        return card;
    }

    /** Returns the total number of customers as a string. */
    private String countCustomers() {
        try { return String.valueOf(customerService.getAllCustomers().size()); }
        catch (Exception e) { return "—"; }
    }

    /** Returns the total number of accounts as a string. */
    private String countAccounts() {
        try { return String.valueOf(accountService.getAllAccounts().size()); }
        catch (Exception e) { return "—"; }
    }

    /** Returns the total number of transactions as a string. */
    private String countTransactions() {
        try { return String.valueOf(transactionService.getAllTransactions().size()); }
        catch (Exception e) { return "—"; }
    }

    /** Creates a styled navigation button. */
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(260);
        button.setPrefHeight(45);
        button.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: #1A1A2E; " +
                        "-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 5;");
        return button;
    }
}
