package example.api.config;

import example.api.model.Status;

public class ConsolFormat implements ConsoleFormatter{
    @Override
    public String format(Status status, String message) {
        String typeMethodColor = "\u001B[34m";
        String inMethodColor = "\u001B[32m";
        String successColor = "\u001B[32;1m";
        String errorColor = "\u001B[31m";
        String resetColor = "\u001B[0m";
        String finalMessage = "";

        return switch (status) {
            case SUCCESS -> finalMessage = successColor + message + resetColor;
            case ERROR -> finalMessage = errorColor + message + resetColor;
            case METHOD_TYPE -> finalMessage = typeMethodColor + message + resetColor;
            case IN_METHOD -> finalMessage = inMethodColor + message + resetColor;
        };
    }
}
