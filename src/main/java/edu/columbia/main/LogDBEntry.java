package edu.columbia.main;

import java.io.Serializable;

/**
 * Created by Gideon on 2/23/15.
 */
public class LogDBEntry implements Serializable {

    private String id;
    private String url;

    public LogDBEntry(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public LogDBEntry(String line) {

        //id: 569295949648027650, url:http://twitter.com/465714163/status/569295949648027650

        String [] parts = line.split(",");
        parts[0] = parts[0].replaceAll("id: ","");
        parts[1] = parts[1].replaceAll("url:", "");

        this.id = parts[0];
        this.url = parts[1];

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
