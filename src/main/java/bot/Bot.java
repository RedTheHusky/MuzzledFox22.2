package bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.neovisionaries.ws.client.WebSocketFactory;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.cache.CouchbaseCacheManagerBuilderCustomizer;
import org.springframework.security.web.savedrequest.Enumerator;
import util.*;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {
    private PropertyReader properties;
    private Log logger;
    private ShardManager shardManager;

    public Bot() throws IOException {
        properties = new PropertyReader(new PropertyResourceBundle(Files.newInputStream(Paths.get("resources/discord/properties/config.properties"))));
        logger = new Log();
    }

    public void start() throws Exception {
        try {
            DefaultShardManagerBuilder defaultShardManagerBuilder = DefaultShardManagerBuilder.create(properties.getValue("bot.token"), EnumSet.allOf(GatewayIntent.class));

            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequestsPerHost(1);

            ConnectionPool connectionPool = new ConnectionPool(5, 10, TimeUnit.SECONDS);
            OkHttpClient.Builder httpClientBuilder=new OkHttpClient.Builder().connectionPool(connectionPool).dispatcher(dispatcher);

            defaultShardManagerBuilder.setHttpClientBuilder(httpClientBuilder);
            defaultShardManagerBuilder.setWebsocketFactory(getWSFactory());

            defaultShardManagerBuilder.setShardsTotal(1);
            defaultShardManagerBuilder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);
            defaultShardManagerBuilder.disableCache(CacheFlag.ACTIVITY,CacheFlag.CLIENT_STATUS,CacheFlag.ONLINE_STATUS);

            CommandClientBuilder client = new CommandClientBuilder();
            client.setOwnerId(properties.getValue("bot.owner"));

            client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
            client.useHelpBuilder(false);
            client.setHelpWord("help");
            client.setAlternativePrefix("!>");

            Activity activity = Activity.playing(" with cookies!");
            defaultShardManagerBuilder.setActivity(activity);
            client.setActivity(activity);
            defaultShardManagerBuilder.setStatus(OnlineStatus.OFFLINE);
            client.setStatus(OnlineStatus.OFFLINE);
            defaultShardManagerBuilder.setAutoReconnect(true);
            shardManager=defaultShardManagerBuilder.build();
        } catch(Exception e) {
            logger.error(e);
        }
    }

    private @Nullable WebSocketFactory getWSFactory() throws Exception {
        try {
//            WebSocketFactory wsFactory = new WebSocketFactory();
//            int connTimeout = Integer.parseInt(properties.getValue("bot.websocket.connection_timeout"));
//            int sockTimeout = Integer.parseInt(properties.getValue("bot.websocket.socket_timeout"));
//            wsFactory.setConnectionTimeout(connTimeout);
//            wsFactory.setSocketTimeout(sockTimeout);
//            return getWSFactory();
        } catch(Exception e) {
            logger.error(e);
        }
        return null;
    }
}
