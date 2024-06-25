package example.api.controller;

import example.api.config.ConsoleFormatter;
import example.api.config.JSonManager;
import example.api.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/shutdown")
public class ShutdownController {

    @Autowired
    private final JSonManager jSonManager;
    @Autowired
    private final Logger log;
    @Autowired
    private final ConsoleFormatter console;

    private ShutdownController(
            JSonManager jSonManager,
            Logger log,
            ConsoleFormatter console
    ) {
        this.jSonManager = jSonManager;
        this.log = log;
        this.console = console;
    }

    @Autowired
    private ConfigurableApplicationContext context;

    @PostMapping("/exit")
    public String shutdownAndExit() {
        // Perform cleanup tasks here (e.g., close database connections, release resources)

        // Respond to client with shutdown message
        String message = jSonManager.addLine("warning", "Shutdown process initiated. Exiting application...").build();

        // Initiate shutdown process
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Example: Wait for 5 seconds before shutting down
                log.info(console.format(Status.IN_METHOD, "API SHUTTING DOWN"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            context.close(); // Close the Spring ApplicationContext, effectively shutting down the application
        }).start();

        return message;
    }
}
