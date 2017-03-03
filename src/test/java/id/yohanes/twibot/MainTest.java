package id.yohanes.twibot;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by yohanesgultom on 07/06/16.
 */
public class MainTest {

    @Test
    public void botTest() {
        try {
            Main main = new Main();
            String[] inputs = new String[]{ "hi", "how are you?", "what is your name?", "what do you do?" };
            System.out.println();
            System.out.println("Begin test:\n");
            for (String input:inputs) {
                String response = main.getResponse(input);
                System.out.println("Test > " + input);
                System.out.println("Bot  > " + response);
                Assert.assertNotEquals("I have no answer for that.", response);
                System.out.println();
            }
        } catch (Exception e) {
            assert(false);
        }
    }

}
