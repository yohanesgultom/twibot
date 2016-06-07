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
            String response = main.getResponse("hi");
            System.out.println(response);
            Assert.assertNotEquals("I have no answer for that.", response);
        } catch (Exception e) {
            assert(false);
        }
    }

}
