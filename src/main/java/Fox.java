import bot.Bot;
import util.Log;

public class Fox {
    private static Log logger = new Log();

    public static void main(String[] args) throws Exception {
        try {
            throw new Exception("This is a test");
        } catch(Exception e) {
            logger.error(e);
        }
    }
}
