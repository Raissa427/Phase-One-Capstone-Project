package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Customer;
import com.igirepay.lab2.service.TransactionService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.UUID;

/**
 * Screen for sending money to another customer by phone number.
 * A unique reference ID is auto-generated for each transaction.
 */
public class SendMoneyScreen {

    private final TransactionService transactionService = new TransactionService();

    /** Builds and displays the send money screen. */
    public void show(Stage stage, Customer customer) {
        // --- Title ---
        Label title = new Label("Send Money");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #FFCC00;");

        // --- Input fields ---
        TextField receiverPhoneField = new TextField();
        receiverPhoneField.setPromptText("Receiver Phone Number");
        styleInput(receiverPhoneField);

        TextField amountField = new TextField();
        amountField.setPromptText("Amount (RWF)");
        styleInput(amountField);

        // --- Auto-generated reference ID (read-only) ---
        Label refLabel = new Label("Reference ID:");
        refLabel.setStyle("-fx-text-fill: #AAAAAA;");

        Label refIdLabel = new Label(UUID.randomUUID().toString());
        refIdLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 11px;");

        // --- Buttons ---
        Button sendButton = new Button("Send");
        styleButton(sendButton, "#FFCC00", "#1A1A2E");

        Button backButton = new Button("← Back");
        styleButton(backButton, "#16213E", "#FFCC00");

        // --- Layout ---
        VBox layout = new VBox(15, title, receiverPhoneField, amountField,
                               refLabel, refIdLabel, sendButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Send button action ---
        sendButton.setOnAction(e -> {
            String referenceId = refIdLabel.getText();
            try {
                double amount = Double.parseDouble(amountField.getText());
                transactionService.transfer(customer, receiverPhoneField.getText(), amount, referenceId);
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Sent " + String.format("RWF %.2f", amount) + " successfully.");
                new DashboardScreen().show(stage, customer);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid amount.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Transfer Failed", ex.getMessage());
            }
        });

        backButton.setOnAction(e -> new DashboardScreen().show(stage, customer));

        stage.setScene(new Scene(layout, 420, 420));
        stage.setTitle("IgirePay — Send Money");
        stage.show();
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
