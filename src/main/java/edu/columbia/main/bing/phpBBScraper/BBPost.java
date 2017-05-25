package edu.columbia.main.bing.phpBBScraper;

/**
 * Created by Gideon on 4/27/15.
 */

/**
 * A forum post
 */
public class BBPost {
    private String content;
    private String id;

    public BBPost(String content, String id) {
        this.content = content;
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
