package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Customer;
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
 * The login screen — first screen the user sees.
 * Accepts phone number and PIN, then routes to the correct dashboard.
 */
public class LoginScreen {

    private final CustomerService customerService = new CustomerService();

    /** Builds and displays the login screen on the given stage. */
    public void show(Stage stage) {
        // --- Logo image ---
        ImageView logoImage = loadLogo();

        Label subtitle = new Label("Mobile Money Gateway");
        subtitle.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px;");

        // --- Input fields ---
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        styleInput(phoneField);

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("PIN (5 digits)");
        pinField.setMaxWidth(300);
        styleInput(pinField);

        // --- Login button ---
        Button loginButton = new Button("Login");
        styleButton(loginButton);

        // --- Link to register ---
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register");
        registerLink.setStyle("-fx-text-fill: #FFCC00;");

        // --- Layout ---
        VBox layout = new VBox(15, logoImage, subtitle, phoneField, pinField, loginButton, registerLink);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #1A1A2E;");

        // --- Login button action ---
        loginButton.setOnAction(e -> handleLogin(stage, phoneField.getText(), pinField.getText()));

        // --- Register link action ---
        registerLink.setOnAction(e -> new RegisterScreen().show(stage));

        stage.setScene(new Scene(layout, 420, 500));
        stage.setTitle("IgirePay — Login");
        stage.show();
    }

    /**
     * Loads the logo image from the resources folder.
     * Falls back to a plain text label if the image cannot be found.
     */
    private ImageView loadLogo() {
        try {
            Image image = new Image(
                getClass().getResourceAsStream("/com/igirepay/logo-igire.png")
            );
            ImageView view = new ImageView(image);
            view.setFitWidth(180);
            view.setPreserveRatio(true);
            return view;
        } catch (Exception e) {
            // If image fails to load, return an empty ImageView (subtitle still shows)
            return new ImageView();
        }
    }

    /**
     * Calls CustomerService to validate credentials.
     * Routes to AdminDashboard if ADMIN, or Dashboard if USER.
     */
    private void handleLogin(Stage stage, String phone, String pin) {
        try {
            Customer customer = customerService.login(phone, pin);

            if ("ADMIN".equals(customer.getRole())) {
                new AdminDashboardScreen().show(stage, customer);
            } else {
                new DashboardScreen().show(stage, customer);
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", ex.getMessage());
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
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle().replace("#FFCC00", "#FFD700")));
        button.setOnMouseExited(e  -> button.setStyle(button.getStyle().replace("#FFD700", "#FFCC00")));
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
