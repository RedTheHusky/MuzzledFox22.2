package bot;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Arrays;

public class launch {
    private Logger logger = Logger.getLogger(getClass());
    public static void main(String[] args) throws Exception {
        String fName="[main]";
        Logger logger = Logger.getLogger(launch.class);
        try {
            PropertyConfigurator.configure("resources/logs.properties");
        }catch(Exception e)
        {
            logger.error(fName + ".exception=" + e + " StackTrace:" + Arrays.toString(e.getStackTrace()));

        }
        Bot bot=new Bot();
        bot.startup();
        iBot.bots.put(0,bot);
    }
}
