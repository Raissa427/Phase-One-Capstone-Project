module com.igirepay.igirepay {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.igirepay to javafx.fxml;
    exports com.igirepay;

    opens com.igirepay.lab3.ui to javafx.fxml;
    exports com.igirepay.lab3.ui;

    exports com.igirepay.lab1.model;
    exports com.igirepay.lab2.service;
    exports com.igirepay.lab2.dao;
    exports com.igirepay.lab2.daoImpl;
    exports com.igirepay.lab2.db;
    exports com.igirepay.lab3.exception;
}
