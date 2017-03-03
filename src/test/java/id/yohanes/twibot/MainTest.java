package id.yohanes.twibot;

import org.junit.BeforeClass;
import org.junit.Test;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MainTest {

    private static Main main;
    private static TwitterService twitterServiceMock;

    @BeforeClass
    public static void before() throws Exception {
        main = new Main(System.getProperty("user.dir"), "jdbc:h2:mem:test");
        twitterServiceMock = mock(TwitterService.class);
        main.setTwitter(twitterServiceMock);
    }

    @Test
    public void botTest() {
        try {
            String[] inputs = new String[]{ "hi", "how are you?", "what is your name?", "what do you do?" };
            System.out.println();
            System.out.println("Begin test:\n");
            for (String input:inputs) {
                String response = main.getResponse(input);
                System.out.println("Test > " + input);
                System.out.println("Bot  > " + response);
                assertNotEquals("I have no answer for that.", response);
                System.out.println();
            }
        } catch (Exception e) {
            assert(false);
        }
    }

    @Test
    public void splitTweetTest() {
        assertArrayEquals(
                new String[]{
                        "Sed id suscipit nulla, vel rhoncus neque."
                },
                main.splitTweet("Sed id suscipit nulla, vel rhoncus neque.", "12345678")
        );

        assertArrayEquals(
                new String[]{
                        "Sed id suscipit nulla, vel rhoncus neque. Quisque eu leo et lacus pharetra blandit. Ut in finibus turpis. Sed tincidunt sed mauris",
                        "in vehicula nullam."
                },
                main.splitTweet("Sed id suscipit nulla, vel rhoncus neque. Quisque eu leo et lacus pharetra blandit. Ut in finibus turpis. Sed tincidunt sed mauris in vehicula nullam.", "12345678")
        );

        assertArrayEquals(
                new String[]{
                        "Mauris arcu quam, vestibulum vitae dolor vitae, congue viverra purus. Nulla ac ultricies metus. Quisque porttitor turpis et leo",
                        "porttitor, in hendrerit felis placerat. Aliquam et euismod libero. Ut tortor velit, interdum id nibh vel, iaculis luctus tortor.",
                        "Phasellus augue ipsum, cursus sit amet ex in, dictum blandit nibh. Maecenas venenatis sollicitudin tincidunt. Lorem ipsum dolor",
                        "sit amet, consectetur adipiscing elit. Mauris leo diam, posuere eget risus sed, commodo pellentesque nisi. Morbi id turpis augue",
                        "metus."
                },
                main.splitTweet("Mauris arcu quam, vestibulum vitae dolor vitae, congue viverra purus. Nulla ac ultricies metus. Quisque porttitor turpis et leo porttitor, in hendrerit felis placerat. Aliquam et euismod libero. Ut tortor velit, interdum id nibh vel, iaculis luctus tortor. Phasellus augue ipsum, cursus sit amet ex in, dictum blandit nibh. Maecenas venenatis sollicitudin tincidunt. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris leo diam, posuere eget risus sed, commodo pellentesque nisi. Morbi id turpis augue metus.", "12345678")
        );

    }

    @Test
    public void runTest() {
        try {
            long expectedLastId = 1L;
            for (long i = 2L; i < 10L; i++) {
                long lastId = main.getLastTweet();
                assertEquals(expectedLastId, lastId);

                // mock mentions
                Paging paging = new Paging(lastId);
                Status statusMock = mock(Status.class);
                User userMock = mock(User.class);
                when(userMock.getScreenName()).thenReturn("user");
                when(statusMock.getId()).thenReturn(i);
                when(statusMock.getText()).thenReturn("@bot hi");
                when(statusMock.getUser()).thenReturn(userMock);
                when(statusMock.getCreatedAt()).thenReturn(new Date());
                ArrayList<Status> mentions = new ArrayList<>();
                mentions.add(statusMock);
                when(twitterServiceMock.getMentionsTimeline(paging)).thenReturn(mentions);

                // mock updated status
                String response = main.getResponse("hi");
                StatusUpdate statusUpdate = new StatusUpdate(String.format("@%s %s", "user", response));
                statusUpdate.setInReplyToStatusId(lastId);

                Status updatedStatusMock = mock(Status.class);
                User bot = mock(User.class);
                when(bot.getScreenName()).thenReturn("bot");
                when(updatedStatusMock.getId()).thenReturn(i + 1);
                when(updatedStatusMock.getText()).thenReturn(statusUpdate.getStatus());
                when(updatedStatusMock.getUser()).thenReturn(bot);
                when(twitterServiceMock.updateStatus(statusUpdate)).thenReturn(updatedStatusMock);

                // test run
                main.run();
                expectedLastId = i;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }
}
