package edu.columbia.main.collection;

import edu.columbia.main.LogDB;

/**
 * Created by Gideon on 4/8/15.
 */
public class BabelJob {

    protected final LogDB logDb;
    protected final LogDB usersLogDB;

    protected String data;
    protected String lang;

    public BabelJob(String jsonData, String lang, LogDB logDb, LogDB usersLogDB) {
        this.logDb = logDb;
        data = jsonData;
        this.lang = lang;
        this.usersLogDB = usersLogDB;
    }

    public LogDB getDB() {
        return logDb;
    }

    public LogDB getUsersLogDB(){
        return usersLogDB;
    }

    public String getLanguage() {
        return lang;
    }

    public boolean hasResulst() {
        return !data.contains("\"list\": [],");
    }

    public String getData(){
        return data;
    }
}
