package edu.columbia.main.db.Models;

import com.mongodb.BasicDBObject;
import org.joda.time.DateTime;

/**
 * Created by Gideon on 9/22/15.
 */

/**
 * POJO for Blog Posts
 */
public class BlogPost extends DBEntry {

    public BlogPost(String data, String language, DateTime dateTime, String source, String url, String guid, String filename, boolean optimized) {
        super(filename);
        setData(data);
        setLanguage(language);
        setDateTime(dateTime);
        setSource(source);
        setUrl(url);
        setGUID(guid);
        setOptimized(optimized);
    }

    public BlogPost(String data, String language, DateTime dateTime, String source, String url, String guid, String filename) {
        super(filename);
        setData(data);
        setLanguage(language);
        setDateTime(dateTime);
        setSource(source);
        setUrl(url);
        setGUID(guid);
    }


    public BlogPost(BasicDBObject cur) {
        super(cur);
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

    public String getGUID() {
        return (String) get("guid");
    }
    public void setGUID(String id) {
        put("guid",id);
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

    public void setOptimized(boolean optimized) {
        put("optimized",String.valueOf(true));
    }

    public boolean getOptimized(boolean optimized) {
        if (containsField("optimized"))
            return Boolean.valueOf((String)get("optimized"));
        else
            return false;
    }
}
