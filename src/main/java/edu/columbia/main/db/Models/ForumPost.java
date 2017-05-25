package edu.columbia.main.db.Models;

import org.joda.time.DateTime;

/**
 * Created by Gideon on 9/22/15.
 */

/**
 * POJO For forum posts
 */
public class ForumPost extends DBEntry {

    public ForumPost(String data, String language, DateTime dateTime, String source, String url, String origID, String fileName) {
        super(fileName);
        setData(data);
        setLanguage(language);
        setDateTime(dateTime);
        setSource(source);
        setUrl(url);
        setOrignalPostID(origID);
    }

    public String getLanguage() {
        return (String) get("languageCode");
    }

    public void setLanguage(String language) {
        put("languageCode",language);
    }


    public DateTime getDateTime() {
        return new DateTime((String)get("dateTime"));
    }

    public void setDateTime(DateTime dateTime) {
        if(dateTime == null)
            dateTime = new DateTime();
        put("dateTime",dateTime.toString());
    }

    public String getSource() {
        return (String) get("source");
    }

    public void setSource(String source) {
        put("source",source);
    }

    public String getOrignalPostID() {
        return (String) get("orig_id");
    }
    public void setOrignalPostID(String id) {
        put("orig_id",id);
    }

    public String getId(){
        return (String)get("_id");
    }


    public void setUrl(String url) {
        put("url",url);
    }

    public String getUrl() {
        return (String)get("url");
    }
}
