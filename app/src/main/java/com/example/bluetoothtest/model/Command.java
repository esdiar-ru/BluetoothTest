package com.example.bluetoothtest.model;

public class Command {
    public Command(Mode mode, String message) {
        this.mode = mode;
        this.message = message;
    }

    public Mode getMode() {
        return mode;
    }

    public String getMessage() {
        return message;
    }

    private Mode mode;
    private String message;

    public enum Mode {
        RX,
        TX;
    }

    @Override
    public String toString() {
        return mode +
                ", mes=" + message;
    }
}
