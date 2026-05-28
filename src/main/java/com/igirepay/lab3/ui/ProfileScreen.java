package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Customer;
import com.igirepay.lab2.service.CustomerService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Screen for viewing and updating the customer's profile.
 * Allows updating name/email and changing the PIN.
 */
public class ProfileScreen {

    private final CustomerService customerService = new CustomerService();

    /** Builds and displays the profile screen. */
    public void show(Stage stage, Customer customer) {
        // --- Title ---
        Label title = new Label("My Profile");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #FFCC00;");

        // --- Read-only info labels ---
        Label nameInfo  = new Label("Name:  " + customer.getFullName());
        Label emailInfo = new Label("Email: " + customer.getEmail());
        Label phoneInfo = new Label("Phone: " + customer.getPhoneNumber());
        for (Label label : new Label[]{nameInfo, emailInfo, phoneInfo}) {
            label.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px;");
        }

        Separator sep1 = new Separator();

        // --- Update name and email section ---
        Label updateTitle = new Label("Update Profile");
        updateTitle.setStyle("-fx-text-fill: #AAAAAA;");

        TextField newNameField  = new TextField(customer.getFullName());
        styleInput(newNameField);

        TextField newEmailField = new TextField(customer.getEmail());
        styleInput(newEmailField);

        Button saveBtn = new Button("Save Changes");
        styleButton(saveBtn, "#FFCC00", "#1A1A2E");

        Separator sep2 = new Separator();

        // --- Change PIN section ---
        Label pinTitle = new Label("Change PIN");
        pinTitle.setStyle("-fx-text-fill: #AAAAAA;");

        PasswordField oldPinField     = new PasswordField();
        oldPinField.setPromptText("Current PIN");
        styleInput(oldPinField);

        PasswordField newPinField     = new PasswordField();
        newPinField.setPromptText("New PIN (5 digits)");
        styleInput(newPinField);

        PasswordField confirmPinField = new PasswordField();
        confirmPinField.setPromptText("Confirm New PIN (5 digits)");
        styleInput(confirmPinField);

        Button changePinBtn = new Button("Change PIN");
        styleButton(changePinBtn, "#16213E", "#FFCC00");

        // --- Back button ---
        Button backButton = new Button("← Back");
        styleButton(backButton, "#16213E", "#FFCC00");

        // --- Layout ---
        VBox layout = new VBox(10, title, nameInfo, emailInfo, phoneInfo, sep1,
                               updateTitle, newNameField, newEmailField, saveBtn, sep2,
                               pinTitle, oldPinField, newPinField, confirmPinField,
                               changePinBtn, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Save profile action ---
        saveBtn.setOnAction(e -> {
            try {
                customerService.updateCustomerInfo(customer, newNameField.getText(), newEmailField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
                show(stage, customer); // refresh with updated data
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Update Failed", ex.getMessage());
            }
        });

        // --- Change PIN action ---
        changePinBtn.setOnAction(e -> {
            if (!newPinField.getText().equals(confirmPinField.getText())) {
                showAlert(Alert.AlertType.ERROR, "PIN Mismatch", "New PINs do not match.");
                return;
            }
            // Enforce exactly 5 digits before calling the service
            if (!newPinField.getText().matches("\\d{5}")) {
                showAlert(Alert.AlertType.ERROR, "Invalid PIN", "PIN must be exactly 5 digits.");
                return;
            }
            try {
                customerService.changePin(customer, oldPinField.getText(), newPinField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Success", "PIN changed successfully.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "PIN Change Failed", ex.getMessage());
            }
        });

        backButton.setOnAction(e -> new DashboardScreen().show(stage, customer));

        stage.setScene(new Scene(layout, 420, 620));
        stage.setTitle("IgirePay — Profile");
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
