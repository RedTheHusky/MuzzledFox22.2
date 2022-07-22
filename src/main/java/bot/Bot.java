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
import org.springframework.security.web.savedrequest.Enumerator;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {
    private Logger logger = Logger.getLogger(getClass());

    public Bot() {

    }

    public EventWaiter waiter;
    public ScheduledExecutorService waiterThreadpool;
    public ScheduledExecutorService schedule;
    public CommandClient commandClientBuilt;

    public void loadConfiguration() throws IOException {
        String fName="[loadConfiguration]";
        configProperty._set(new PropertyResourceBundle(Files.newInputStream(Paths.get("resources/discord/properties/config.properties"))));
        botconfig._set(configProperty);
    }
    private  CONFIG configProperty=new CONFIG();
    protected class CONFIG{
        ResourceBundle configProperty = null;
        private CONFIG(){

        }
        private CONFIG(ResourceBundle configProperty){
            _set(configProperty);
        }
        private void _set(ResourceBundle configProperty){
            this.configProperty=configProperty;
        }
        public Enumeration<String> getKeys(String key) throws Exception {
            String fName="[getKeys]";
            if(key==null)throw  new Exception("Key is null!");
            if(key.isBlank())throw  new Exception("Key is blank!");
            Enumeration<String> values =  configProperty.getKeys();
            logger.debug(fName+"values="+ values.toString());
            return values;
        }
        public String getString(String key) throws Exception {
            String fName="[getString]";
            if(key==null)throw  new Exception("Key is null!");
            if(key.isBlank())throw  new Exception("Key is blank!");
            logger.debug(fName+"key="+key);
            String value=configProperty.getString(key);
            logger.debug(fName+"value="+value);
            return value;
        }
        public String getString(String key,String valuedef) throws Exception {
            String fName="[getString]";
            try {
                if(key==null)throw  new Exception("Key is null!");
                if(key.isBlank())throw  new Exception("Key is blank!");
                logger.debug(fName+"key="+key);
                String value=configProperty.getString(key);
                logger.debug(fName+"value="+value);
                return value;
            }catch (Exception e){
                logger.warn(fName + ".exception=" + e+", StackTrace="+Arrays.toString(e.getStackTrace()));
                if(key==null)throw  new Exception("valuedef is null!");
                if(key.isBlank())throw  new Exception("valuedef is blank!");
                return valuedef;
            }

        }
        public String[] getStringArray(String key) throws Exception {
            String fName="[getStringArray]";
            if(key==null)throw  new Exception("Key is null!");
            if(key.isBlank())throw  new Exception("Key is blank!");
            logger.debug(fName+"key="+key);
            String[] value=configProperty.getStringArray(key);
            logger.debug(fName+"value="+ Arrays.toString(value));
            return value;
        }
        public Object getObject(String key) throws Exception {
            String fName="[getObject]";
            if(key==null)throw  new Exception("Key is null!");
            if(key.isBlank())throw  new Exception("Key is blank!");
            logger.debug(fName+"key="+key);
            Object value=configProperty.getObject(key);
            logger.debug(fName+"value="+ value);
            return value;
        }
        public Integer getInt(String key) throws Exception {
            String fName="[getInt]";
            logger.debug(fName+"key="+key);
            Integer value=Integer.parseInt(getString(key));
            logger.debug(fName+"value="+value);
            return value;
        }
        public Integer getInt(String key, int valuedef)  {
            String fName="[getInt]";
            try {
                logger.debug(fName+"key="+key);
                Integer value=Integer.parseInt(getString(key));
                logger.debug(fName+"value="+value);
                return value;
            }catch (Exception e){
                logger.warn(fName + ".exception=" + e+", StackTrace="+Arrays.toString(e.getStackTrace()));
                return valuedef;
            }
        }
        public Long getLong(String key) throws Exception {
            String fName="[getLong]";
            logger.debug(fName+"key="+key);
            Long value=Long.parseLong(getString(key));
            logger.debug(fName+"value="+value);
            return value;
        }
        public Long getLong(String key,long valuedef)  {
            String fName="[getLong]";
            try {
                logger.debug(fName+"key="+key);
                Long value=Long.parseLong(getString(key));
                logger.debug(fName+"value="+value);
                return value;
            }catch (Exception e){
                logger.warn(fName + ".exception=" + e+", StackTrace="+Arrays.toString(e.getStackTrace()));
                return valuedef;
            }
        }
    }
    private  BOTCONFIG botconfig=new BOTCONFIG();
    protected class BOTCONFIG{
        CONFIG config = null;
        private BOTCONFIG(){

        }
        private BOTCONFIG(CONFIG config){
            _set(config);
        }
        private void _set(CONFIG config){
            this.config=config;
        }
        public String getID() throws Exception {return config.getString("bot.id");}
        public String getToken() throws Exception {return config.getString("bot.token");}
        public String getSecret() throws Exception {return config.getString("bot.secret");}
        public int getWaiter() throws Exception {return config.getInt("bot.waiter",1);}
    }
    private void setGeneralEventWaiter() throws Exception {
        String fName="[setGeneralEventWaiter]";
        waiterThreadpool= Executors.newScheduledThreadPool(botconfig.getWaiter());
        waiter=new EventWaiter(waiterThreadpool,true);
        logger.warn(fName + ".done");
    }
    public void setScheduleThreadpool(){
        String fName="[setScheduleThreadpool]";
        int count=1;
        schedule =Executors.newScheduledThreadPool(count);
        logger.warn(fName + ".done");
    }
    private ShardManager shardManager=null;
    public void startup() throws Exception {
        Logger logger = Logger.getLogger(Bot.class);
        String fName="[startup]";
        loadConfiguration();

        CommandClientBuilder client = new CommandClientBuilder();
        loadConfiguration();
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(botconfig.getToken(), EnumSet.allOf(GatewayIntent.class));
        try {
            WebSocketFactory webSocketFactory=new WebSocketFactory();
            webSocketFactory.setConnectionTimeout(configProperty.getInt("bot.websocket.connection_timeout"));
            webSocketFactory.setSocketTimeout(configProperty.getInt("bot.websocket.socket_timeout"));
            builder.setWebsocketFactory(webSocketFactory);
            logger.warn(fName + ": load custom.webSocketFactory connectionTimeout=" + webSocketFactory.getConnectionTimeout()+", socketTimeout="+webSocketFactory.getSocketTimeout());
        }catch (Exception e){
            logger.error(fName + ".exception=" + e+", StackTrace="+Arrays.toString(e.getStackTrace()));
        }
        try {
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequestsPerHost(configProperty.getInt("bot.httpclient.max_requests_per_host",25));
            ConnectionPool connectionPool = new ConnectionPool(configProperty.getInt("bot.httpclient.max_idle_connection",5), configProperty.getLong("bot.httpclient.keep_alive_duration",10L), TimeUnit.SECONDS);
            OkHttpClient.Builder httpClientBuilder=new OkHttpClient.Builder().connectionPool(connectionPool).dispatcher(dispatcher);
            if(httpClientBuilder==null)throw new Exception("Failed to build custom httpClientBuilder");
            builder.setHttpClientBuilder(httpClientBuilder);
            logger.warn(fName + ": load custom.httpClientBuilder maxRequestsPerHost=" + configProperty.getInt("bot.httpclient.max_requests_per_host",25)+", maxIdleConnections="+configProperty.getInt("bot.httpclient.max_idle_connection",5)+", keepAlliveDuration(seconds)="+configProperty.getLong("bot.httpclient.keep_alive_duration",10L));

        }catch (Exception e){
            logger.error(fName + ".exception=" + e+", StackTrace="+Arrays.toString(e.getStackTrace()));
        }

        builder.setShardsTotal(configProperty.getInt("bot.shards",1));
        logger.warn(fName + ": set.shards=" + configProperty.getInt("bot.shards",1));
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.disableCache(CacheFlag.ACTIVITY,CacheFlag.CLIENT_STATUS,CacheFlag.ONLINE_STATUS);

        try {
            client.setOwnerId(configProperty.getString("bot.owner"));
        }catch (Exception e){
            logger.error(fName + ".exception=" + e+", StackTrace="+Arrays.toString(e.getStackTrace()));
        }

        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        client.useHelpBuilder(false);
        client.setHelpWord("help");
        client.setAlternativePrefix("!>");
        logger.warn(fName + ": adding commands");

        logger.warn(fName + ": commands added->adding events");

        logger.warn(fName + ": events added->set activity");
        Activity activity = Activity.playing(" with cookies!");
        builder.setActivity(activity);
        client.setActivity(activity);
        builder.setStatus(OnlineStatus.OFFLINE);
        client.setStatus(OnlineStatus.OFFLINE);
        logger.warn(fName + " done setting up activity->creating client");
        CommandClient commandClientBuilt = client.build();
        setGeneralEventWaiter();setScheduleThreadpool();
        builder.addEventListeners(waiter, commandClientBuilt);
        builder.setAutoReconnect(true);
        shardManager=builder.build();
        logger.warn(fName + ":Sharding built");
        this.commandClientBuilt = commandClientBuilt;
        logger.warn(fName + ":Bot loaded");
    }
}
