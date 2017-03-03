package id.yohanes.twibot;

import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class TwitterService {

    private Twitter twitter;

    public TwitterService() {
        this.twitter = TwitterFactory.getSingleton();
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public List<Status> getMentionsTimeline(Paging paging) throws TwitterException {
        List<Status> mentions = new ArrayList<>();
        ResponseList<Status> responses = this.twitter.getMentionsTimeline(paging);
        for (Status status:responses) {
            mentions.add(status);
        }
        return mentions;
    }

    public Status updateStatus(StatusUpdate statusUpdate) throws TwitterException {
        return this.twitter.updateStatus(statusUpdate);
    }
}
