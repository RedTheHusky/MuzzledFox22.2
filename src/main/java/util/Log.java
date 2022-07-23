package util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerRepository;

public class Log extends Logger {
    public Log() {
        super("");
        PropertyConfigurator.configure("resources/log4j.properties");
    }

    public void error(Exception e) {
        String classAndFile = String.valueOf(e.getStackTrace()[0]).replaceAll("(\\(.*\\))", "");
        super.error(classAndFile + ": " + e);
    }
}
