package com.igirepay;

import com.igirepay.lab2.service.CustomerService;
import com.igirepay.lab3.ui.LoginScreen;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for the IgirePay application.
 * Seeds the default admin account on first run, then shows the login screen.
 */
public class main extends Application {

    private static final String ADMIN_PHONE = "0784728924";
    private static final String ADMIN_PIN   = "12345";

    /** Called by JavaFX after the application is initialized. */
    @Override
    public void start(Stage primaryStage) {
        seedAdminAccount();
        new LoginScreen().show(primaryStage);
    }

    /**
     * Creates the default admin account if it does not already exist.
     * This runs once every time the app starts — it skips silently if the admin is already there.
     */
    private void seedAdminAccount() {
        try {
            CustomerService customerService = new CustomerService();
            customerService.registerCustomer(
                    "Admin",
                    "admin@igirepay.com",
                    ADMIN_PHONE,
                    ADMIN_PIN,
                    "ADMIN"
            );
        } catch (Exception e) {
            // If the admin already exists, registerCustomer throws "Phone already registered"
            // We silently ignore that — it just means the admin was already seeded before.
        }
    }

    /** Main method — launches the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
