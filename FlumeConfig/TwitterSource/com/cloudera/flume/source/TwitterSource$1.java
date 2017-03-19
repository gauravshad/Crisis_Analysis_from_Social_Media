//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cloudera.flume.source;

import com.cloudera.flume.source.TwitterSource;
import java.util.Map;
import org.apache.flume.Event;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.event.EventBuilder;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.json.DataObjectFactory;

class TwitterSource$1 implements StatusListener {
    TwitterSource$1(TwitterSource var1, Map var2, ChannelProcessor var3) {
        this.this$0 = var1;
        this.val$headers = var2;
        this.val$channel = var3;
    }

    public void onStatus(Status status) {
        TwitterSource.access$000().debug(status.getUser().getScreenName() + ": " + status.getText());
        this.val$headers.put("timestamp", String.valueOf(status.getCreatedAt().getTime()));
        Event event = EventBuilder.withBody(DataObjectFactory.getRawJSON(status).getBytes(), this.val$headers);
        this.val$channel.processEvent(event);
    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
    }

    public void onScrubGeo(long userId, long upToStatusId) {
    }

    public void onException(Exception ex) {
    }

    public void onStallWarning(StallWarning warning) {
    }
}
