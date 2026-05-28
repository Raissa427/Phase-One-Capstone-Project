package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab2.service.AccountService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/**
 * The main dashboard for regular USER customers.
 * Shows the wallet balance and navigation buttons for all features.
 */
public class DashboardScreen {

    private final AccountService accountService = new AccountService();

    /** Builds and displays the user dashboard on the given stage. */
    public void show(Stage stage, Customer customer) {
        // --- Welcome message ---
        Label welcome = new Label("Welcome, " + customer.getFullName());
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        welcome.setStyle("-fx-text-fill: #FFCC00;");

        // --- Balance card ---
        Label balanceLabel = new Label("Wallet Balance");
        balanceLabel.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 13px;");

        Label balanceAmount = new Label(loadBalance(customer));
        balanceAmount.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        balanceAmount.setStyle("-fx-text-fill: #FFFFFF;");

        VBox balanceCard = new VBox(5, balanceLabel, balanceAmount);
        balanceCard.setAlignment(Pos.CENTER);
        balanceCard.setPadding(new Insets(20));
        balanceCard.setStyle("-fx-background-color: #16213E; -fx-background-radius: 10;");

        // --- Navigation buttons in a 2-column grid ---
        Button sendBtn     = createNavButton("Send Money");
        Button depositBtn  = createNavButton("Deposit");
        Button withdrawBtn = createNavButton("Withdraw");
        Button savingsBtn  = createNavButton("Savings");
        Button loanBtn     = createNavButton("Loan");
        Button historyBtn  = createNavButton("History");
        Button profileBtn  = createNavButton("Profile");
        Button logoutBtn   = createNavButton("Logout");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(sendBtn,     0, 0); grid.add(depositBtn,  1, 0);
        grid.add(withdrawBtn, 0, 1); grid.add(savingsBtn,  1, 1);
        grid.add(loanBtn,     0, 2); grid.add(historyBtn,  1, 2);
        grid.add(profileBtn,  0, 3); grid.add(logoutBtn,   1, 3);

        // --- Main layout ---
        VBox layout = new VBox(20, welcome, balanceCard, grid);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Button actions ---
        sendBtn.setOnAction(e     -> new SendMoneyScreen().show(stage, customer));
        depositBtn.setOnAction(e  -> new DepositScreen().show(stage, customer));
        withdrawBtn.setOnAction(e -> new WithdrawScreen().show(stage, customer));
        savingsBtn.setOnAction(e  -> new SavingsScreen().show(stage, customer));
        loanBtn.setOnAction(e     -> new LoanScreen().show(stage, customer));
        historyBtn.setOnAction(e  -> new HistoryScreen().show(stage, customer));
        profileBtn.setOnAction(e  -> new ProfileScreen().show(stage, customer));
        logoutBtn.setOnAction(e   -> new LoginScreen().show(stage));

        stage.setScene(new Scene(layout, 460, 600));
        stage.setTitle("IgirePay — Dashboard");
        stage.show();
    }

    /**
     * Loads and formats the wallet balance for the logged-in customer.
     * Returns a placeholder if no wallet account is found.
     */
    private String loadBalance(Customer customer) {
        try {
            List<Account> accounts = accountService.getCustomerAccounts(customer.getId());
            for (Account account : accounts) {
                if ("WALLET".equals(account.getAccountType())) {
                    return String.format("RWF %.2f", account.getBalance());
                }
            }
        } catch (Exception e) {
            showAlert("Could not load balance: " + e.getMessage());
        }
        return "RWF 0.00";
    }

    /** Creates a styled navigation button with a fixed size. */
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(160, 50);
        button.setStyle("-fx-background-color: #16213E; -fx-text-fill: #FFCC00; " +
                        "-fx-font-weight: bold; -fx-border-color: #FFCC00; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle().replace("#16213E", "#FFCC00")
                                                                        .replace("#FFCC00; -fx-font", "#1A1A2E; -fx-font")));
        button.setOnMouseExited(e  -> button.setStyle("-fx-background-color: #16213E; -fx-text-fill: #FFCC00; " +
                                                      "-fx-font-weight: bold; -fx-border-color: #FFCC00; " +
                                                      "-fx-border-radius: 5; -fx-background-radius: 5;"));
        return button;
    }

    /** Shows an error alert dialog. */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
