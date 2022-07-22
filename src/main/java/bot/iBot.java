package bot;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public interface iBot {
    Map<Integer, Bot> bots=new HashMap<>();
    static Bot getBot() throws Exception {
        return bots.get(0);
    }

}
