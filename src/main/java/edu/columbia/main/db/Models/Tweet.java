package edu.columbia.main.db.Models;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.joda.time.DateTime;

/**
 * Created by Gideon on 9/22/15.
 */

/**
 * Pojo for tweet
 */
public class Tweet extends DBEntry {


    public Tweet(String data, String unNormalizedData, String language, String user, DateTime dateTime, String collectedFrom, String url, String twitter_id, String filename) {
        super(filename);
        setData(data);
        setNonNormalizedData(unNormalizedData);
        setLanguage(language);
        setUser(user);
        setDateTime(dateTime);
        setSource(collectedFrom);
        setUrl(url);
        setTwitterId(twitter_id);
    }

    public Tweet(BasicDBObject obj) {
        super(obj);
    }


    public String getLanguage() {
        return (String) get("languageCode");
    }

    public void setLanguage(String language) {
        put("languageCode",language);
    }

    public String getUser() {
        return (String) get("user");
    }

    public void setUser(String user) {
        put("user",user);
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
        return (String) get("collectedFrom");
    }

    public void setSource(String source) {
        put("collectedFrom",source);
    }

    public String getTwitterId() {
        return (String) get("twitter_id");
    }
    public void setTwitterId(String id) {
        put("twitter_id",id);
    }



    public void setNonNormalizedData(String nonNormalizedData) {
        put("non_normalized",nonNormalizedData);
    }

    public void getNonNormalizedData() {
        get("non_normalized");
    }


    public void setUrl(String url) {
        put("url",url);
    }

    public String getUrl() {
        return (String)get("url");
    }


}
