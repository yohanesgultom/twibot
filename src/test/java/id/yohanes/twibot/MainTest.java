package id.yohanes.twibot;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class MainTest {

    private static Main main;

    @BeforeClass
    public static void before() throws Exception {
        main = new Main();
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

}
