package example.api.config;

import example.api.model.Status;

@FunctionalInterface
public interface ConsoleFormatter {
    String format(Status status, String message);
}
