package io.github.jkvely.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MainViewModel {
    private final StringProperty message = new SimpleStringProperty("¡Bienvenido a E-BMaker!");

    public StringProperty messageProperty() {
        return message;
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String value) {
        message.set(value);
    }
}
