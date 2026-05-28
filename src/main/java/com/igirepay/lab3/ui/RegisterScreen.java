package com.igirepay.lab3.ui;

import com.igirepay.lab2.service.CustomerService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * The registration screen — allows a new user to create an account.
 * On success, redirects back to the login screen.
 */
public class RegisterScreen {

    private final CustomerService customerService = new CustomerService();

    /** Builds and displays the registration screen on the given stage. */
    public void show(Stage stage) {
        // --- Logo image ---
        ImageView logoImage = loadLogo();

        // --- Title ---
        Label title = new Label("Create Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #FFCC00;");

        // --- Input fields ---
        TextField nameField  = new TextField();
        nameField.setPromptText("Full Name");
        styleInput(nameField);

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        styleInput(emailField);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        styleInput(phoneField);

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("PIN (5 digits)");
        styleInput(pinField);

        PasswordField confirmPinField = new PasswordField();
        confirmPinField.setPromptText("Confirm PIN");
        styleInput(confirmPinField);

        // --- Register button ---
        Button registerButton = new Button("Register");
        styleButton(registerButton);

        // --- Back to login link ---
        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setStyle("-fx-text-fill: #FFCC00;");

        // --- Layout ---
        VBox layout = new VBox(12, logoImage, title, nameField, emailField, phoneField,
                               pinField, confirmPinField, registerButton, loginLink);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Register button action ---
        registerButton.setOnAction(e -> handleRegister(
                stage,
                nameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                pinField.getText(),
                confirmPinField.getText()
        ));

        // --- Back to login link action ---
        loginLink.setOnAction(e -> new LoginScreen().show(stage));

        stage.setScene(new Scene(layout, 420, 560));
        stage.setTitle("IgirePay — Register");
        stage.show();
    }

    /**
     * Loads the logo image from the resources folder.
     * Falls back to an empty ImageView if the image cannot be found.
     */
    private ImageView loadLogo() {
        try {
            Image image = new Image(
                getClass().getResourceAsStream("/com/igirepay/logo-igire.png")
            );
            ImageView view = new ImageView(image);
            view.setFitWidth(150);
            view.setPreserveRatio(true);
            return view;
        } catch (Exception e) {
            return new ImageView();
        }
    }

    /**
     * Validates that PINs match, then calls CustomerService to register.
     * On success, shows a confirmation and redirects to login.
     */
    private void handleRegister(Stage stage, String name, String email,
                                 String phone, String pin, String confirmPin) {
        // Check that both PINs match before sending to service
        if (!pin.equals(confirmPin)) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "PINs do not match.");
            return;
        }

        // Enforce exactly 5 digits in the UI before calling the service
        if (!pin.matches("\\d{5}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid PIN", "PIN must be exactly 5 digits.");
            return;
        }

        try {
            customerService.registerCustomer(name, email, phone, pin, "USER");
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created! Please login.");
            new LoginScreen().show(stage);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", ex.getMessage());
        }
    }

    /** Applies a consistent style to text input fields. */
    private void styleInput(TextField field) {
        field.setMaxWidth(300);
        field.setStyle("-fx-background-color: #16213E; -fx-text-fill: #FFFFFF; " +
                       "-fx-prompt-text-fill: #888888; -fx-border-color: #FFCC00; " +
                       "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8;");
    }

    /** Applies the primary yellow button style. */
    private void styleButton(Button button) {
        button.setMaxWidth(300);
        button.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: #1A1A2E; " +
                        "-fx-font-weight: bold; -fx-font-size: 14px; " +
                        "-fx-background-radius: 5; -fx-padding: 10;");
    }

    /** Shows an alert dialog with the given type, title, and message. */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
