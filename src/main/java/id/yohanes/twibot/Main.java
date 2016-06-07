package id.yohanes.twibot;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.apache.log4j.Logger;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yohanesgultom on 07/06/16.
 */
public class Main {

    private final static Logger logger = Logger.getLogger(Main.class);
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:~/.twibot";
    private static final String MENTION_FORMAT = "@%s %s";
    private static final String TWIBOT_NAME = "alice2";
    private static final String TWIBOT_ACTION = "chat";

    private Connection dbConnection;
    private Twitter twitter;
    private Bot bot;

    public Main() throws Exception {
        this(System.getProperty("user.dir"));
    }

    public Main(String twibotHome) throws Exception {
        Class.forName(DB_DRIVER);
        this.dbConnection = DriverManager.getConnection(DB_CONNECTION, "sa", "");
        this.twitter = TwitterFactory.getSingleton();
        this.bot = loadBot(twibotHome);
    }

    public Twitter getTwitter() {
        return this.twitter;
    }

    Bot loadBot(String twibotHome) {
        Bot bot = new Bot(TWIBOT_NAME, twibotHome, TWIBOT_ACTION);
        return bot;
    }

    String getResponse(String request) {
        Chat chat = new Chat(this.bot, false);
        String response = chat.multisentenceRespond(request);
        while (response.contains("&lt;")) response = response.replace("&lt;","<");
        while (response.contains("&gt;")) response = response.replace("&gt;",">");
        return response;
    }

    void createDbIfNotExist() throws Exception {
        Statement statement = dbConnection.createStatement();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS tweet("
                + "id BIGINT NOT NULL, "
                + "username VARCHAR(30) NOT NULL, "
                + "text VARCHAR(160) NOT NULL, "
                + "created_date DATE NOT NULL, " + "PRIMARY KEY (id) "
                + ")";
        boolean result = statement.execute(createTableSQL);
        if (result) logger.info(createTableSQL);
        logger.info("Database creation: " + result);
    }

    void insertTweets(List<Status> tweets) throws Exception {
        String insertTableSQL = "INSERT INTO tweet"
                + "(id, username, text, created_date) VALUES"
                + "(?,?,?,?)";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(insertTableSQL);
        int count = 0;
        for (Status tweet:tweets) {
            preparedStatement.setLong(1, tweet.getId());
            preparedStatement.setString(2, tweet.getUser().getScreenName());
            preparedStatement.setString(3, tweet.getText());
            preparedStatement.setDate(4, new java.sql.Date(tweet.getCreatedAt().getTime()));
            try {
                preparedStatement.executeUpdate();
                count++;
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
        logger.info(count + " new tweet inserted");
    }

    long getLastTweet() throws Exception {
        long id = 0;
        String selectTableSQL = "SELECT id FROM tweet ORDER BY created_date DESC LIMIT 1";
        Statement statement = dbConnection.createStatement();
        ResultSet rs = statement.executeQuery(selectTableSQL);
        if (rs.next()) {
            id = rs.getLong("id");
        }
        return id;
    }

    void closeDbConnection() {
        try {
            if (dbConnection != null) dbConnection.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }



    public static void main(String[] args) {
        Main main = null;
        try {
            main = (args.length > 0) ? new Main(args[0]) : new Main();
            main.createDbIfNotExist();
            long lastId = main.getLastTweet();
            logger.info("Last tweet id: " + lastId);
            List<Status> statuses = main.getTwitter().getMentionsTimeline();
            List<Status> newTweets = new ArrayList<>();
            for (Status status:statuses) {
                if (status.getId() > lastId) {
                    newTweets.add(status);
                    logger.info(status.getId() + " " + status.getUser().getName() + ":" + status.getText());
                    try {
                        // replace all usernames
                        String reply = main.getResponse(status.getText().replaceAll("(?i)@[\\w\\d]+ ", ""));
                        logger.info("Reply: " + reply);
                        Status replyStatus = main.getTwitter().updateStatus(String.format(MENTION_FORMAT, status.getUser().getScreenName(), reply));
                        logger.info("Reply status: " + replyStatus);
                    } catch (Exception e1) {
                        logger.warn(e1.getMessage());
                    }
                }
            }
            main.insertTweets(newTweets);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            main.closeDbConnection();
        }
    }

}
