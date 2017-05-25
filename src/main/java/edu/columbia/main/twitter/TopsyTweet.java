package edu.columbia.main.twitter;

/**
 * Created by Gideon on 4/8/15.
 */
public class TopsyTweet {
    public String id;
    public String content;
    public String user;
    private String URL;

    public TopsyTweet(String id, String content, String user, String URL) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.URL = URL;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return content;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
