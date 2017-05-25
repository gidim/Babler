package edu.columbia.main.collection;
import edu.columbia.main.FileSaver;
import edu.columbia.main.db.DAO;
import edu.columbia.main.db.Models.Tweet;
import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.language_id.Result;
import edu.columbia.main.normalization.TwitterNormalizer;
import org.apache.log4j.*;
import edu.columbia.main.twitter.TopsyTweet;
import twitter4j.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gideon on 4/8/15.
 */
public class BabelConsumer implements Runnable {

    protected BabelBroker broker;
    protected LanguageDetector ld;
    protected TwitterNormalizer normalizer;
    protected int i;
    protected Logger log = Logger.getLogger(BabelConsumer.class);

    public BabelConsumer(BabelBroker broker, LanguageDetector ld, int i, TwitterNormalizer normalizer) {
        this.broker = broker;
        this.ld = ld;
        this.normalizer = normalizer;
        this.i = i;
    }


    @Override
    public void run() {
        Thread.currentThread().setName("Parser " + i);

        BabelJob data = null;
        try {

            while (true){
            data = broker.get();
            if (data != null) {
                log.info("new data from broker");
                searchAndSave(data);
            }
        }
        }
        catch (InterruptedException e) {
            log.error(e);
        }
    }


    protected void searchAndSave(BabelJob job){
            List<TopsyTweet> tweets = getTweetsFromJob(job);
            for (TopsyTweet tweet : tweets) {
                try {
                    log.info(job.getLanguage()+"(T),");
                    String content = tweet.getText();
                    String url = "http://twitter.com/" + tweet.getUser() + "/status/" + tweet.getId();
                    String origContent = content;
                    content = normalizer.cleanTweet(content);
                    Result res = ld.detectLanguage(content,job.getLanguage());
                    //System.out.println(content);
                    if (res.languageCode.equals(job.getLanguage()) && res.isReliable) {
                        log.info("\n");
                        FileSaver file = new FileSaver(content, job.getLanguage(), "topsyTwitter", url, String.valueOf(tweet.getId()));
                        String filename = file.getFileName();

                        //save to DB
                        Tweet t = new Tweet(content,origContent,job.getLanguage(),tweet.getUser(),null,"topsyTwitter",tweet.getURL(),tweet.getId(),filename);
                        if(DAO.saveEntry(t))
                            file.save(job.getDB());
                        //log user data
                        job.getUsersLogDB().logWithUrlNonStatic(tweet.getUser(),tweet.getId(),job.getLanguage());

                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    protected List<TopsyTweet> getTweetsFromJob(BabelJob job) {

        ArrayList<TopsyTweet> list = new ArrayList<TopsyTweet>();

        try {
            JSONObject temp = (JSONObject) new JSONObject(job.getData()).get("response");
            JSONArray tmpList = (JSONArray) temp.get("list");

            for(int i = 0 ; i < tmpList.length() ; i++){
                JSONObject tweet = tmpList.getJSONObject(i);
                String url = (String) tweet.get("trackback_permalink");
                String id = url.substring(url.lastIndexOf('/')+1,url.length());
                String user = ((String)tweet.get("trackback_author_nick"));
                String content = (String) tweet.get("content");
                list.add(new TopsyTweet(id,content,user,url));
            }

        } catch (JSONException e) {
            log.error(e);
        }

        return list;

    }

}

