package edu.columbia.main.bing.phpBBScraper;

import edu.columbia.main.LogDB;
import edu.columbia.main.collection.BabelJob;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Created by Gideon on 4/24/15.
 */
public class BBJob extends BabelJob {

    private String threadNumber;
    private String forumNumber;
    private String origData;
    Logger log = Logger.getLogger(BBJob.class);


    public BBJob(String url, String lang, LogDB logDb) {
        super(url, lang, logDb, null);
        origData = data;
        setURL();
    }


    public void setURL(){
        String retValue = "";
        try {
            URL tmp = new URL(this.data);
            retValue = tmp.getHost() + tmp.getPath();
        } catch (MalformedURLException e) {
            log.error(e);
        }

        List<org.apache.http.NameValuePair> params = null;
        try {
            params = URLEncodedUtils.parse(new URI(this.data), "UTF-8");
        } catch (URISyntaxException e) {
            log.error(e);
        }

        for (org.apache.http.NameValuePair param : params) {
            if(param.getName().equals("f")) {
                retValue += "?f=" + param.getValue();
                forumNumber = param.getValue();
            }
            if(param.getName().equals("t"))
                threadNumber = param.getValue();

        }
        this.data = retValue;
    }

    public String getURL() {
        return data;
    }

    public String getThreadNumber() {
        return threadNumber;
    }

    public boolean isValid(){

        if(threadNumber != null && !threadNumber.isEmpty() && forumNumber !=null && !forumNumber.isEmpty())
            return true;
        else
            return false;

    }

    @Override
    public String toString() {
        return this.origData;
    }

    public String getForumNumber() {
        return forumNumber;
    }
}
