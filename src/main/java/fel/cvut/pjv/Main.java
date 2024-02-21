package fel.cvut.pjv;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    public static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        if (args.length > 0) {
            setUpLogger(args[0]);
        } else {
            setUpLogger("OFF");
        }
        MenuController mc = new MenuController();
        mc.setViewWindow();
    }

    private static void setUpLogger(String level) {
        Level logLvl = Level.ALL;
        switch (level) {
            case "severe":
                logLvl = Level.SEVERE;
                System.out.println("Logger level is SEVERE");
                break;
            case "info":
                logLvl = Level.INFO;
                System.out.println("Logger level is INFO");
                break;
            case "config":
                logLvl = Level.CONFIG;
                System.out.println("Logger level is CONFIG");
                break;
            case "fine":
                logLvl = Level.FINE;
                System.out.println("Logger level is FINE");
                break;
            case "finer":
                logLvl = Level.FINER;
                System.out.println("Logger level is FINER");
                break;
            case "finest":
                logLvl = Level.FINEST;
                System.out.println("Logger level is FINEST");
                break;
            case "all":
                logLvl = Level.ALL;
                System.out.println("Logger level is ALL");
                break;
            case "off":
                logLvl = Level.OFF;
                System.out.println("Logger is OFF");
                break;
            default:
                logLvl = Level.WARNING;
                System.out.println("Logger level is WARNING");
                break;
        }
        LogManager.getLogManager().reset();
        LOGGER.setLevel(logLvl);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(logLvl);
        LOGGER.addHandler(handler);
    }
}