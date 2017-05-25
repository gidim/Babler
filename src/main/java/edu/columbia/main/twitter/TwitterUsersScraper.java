package edu.columbia.main.twitter;

import edu.columbia.main.LogDB;
import edu.columbia.main.language_id.LanguageDetector;
import org.apache.log4j.*;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import edu.columbia.main.FileSaver;
import edu.columbia.main.Utils;
import edu.columbia.main.db.DAO;
import edu.columbia.main.db.Models.Tweet;
import edu.columbia.main.language_id.Result;
import edu.columbia.main.normalization.TwitterNormalizer;
import twitter4j.*;

import java.util.*;

/**
 * Created by Gideon on 2/9/16.
 */
public class TwitterUsersScraper {

    private Twitter twitter;

    private String lang;
    private LanguageDetector languageDetector;
    // list of users
    private Set <String> users;
    private LogDB logDb;

    static Logger log = Logger.getLogger(TwitterUsersScraper.class);

    /**
     * Querys the db for a list of users in this.lang and save to list
     * @throws TwitterException
     */

    public TwitterUsersScraper(String l, LanguageDetector ld)
    {
        this.lang = l;
        this.languageDetector = ld;
        this.users= new HashSet<String>();
        this.users = DAO.getUsersWithTweetInLanguage(lang);
        this.logDb =  new LogDB(this.lang); //saving text files
    }

    /**
     * polls a user from the list and run searchAndSave(user) on it
     * @throws TwitterException
     */

    public void scrapeByLanguage() throws TwitterException
    {
        for(String u: users) {
            searchAndSave(u, this.languageDetector, this.lang);
        }
    }

    /**
     * queries the twitter API for that users' tweets, language id each tweet and if correct saves it
     * @param user
     * @param lp
     * @param lang
     * @throws TwitterException
     */
    private void searchAndSave(String user, LanguageDetector lp, String lang) throws TwitterException {
        log.info("Searching for posts of user: " + user);

        HashMap<String,Boolean> map = new HashMap<String,Boolean>();
        int counter = 0;
        int pageno = 1;
        List<Status> tweets = new ArrayList();
        Logger log = Logger.getLogger(TwitterUsersScraper.class);

        //detremine if user contains a username or userId
        long userId = -1l;
        String userName = null;
        try{
            userId = Long.parseLong(user);
        }
        catch (Exception ex){
            userName = user;
        }
        while (true) {
            try {
                int size = tweets.size();
                Paging page = new Paging(pageno++, 100);
                if(userName != null)
                    tweets.addAll(twitter.getUserTimeline(userName, page)); //get ~3200 tweets by user
                else{
                    tweets.addAll(twitter.getUserTimeline(userId, page)); //get ~3200 tweets by user
                }
                if (tweets.size() == size)
                    break;
            }
            catch(TwitterException e) {
                if(e.getErrorCode() == 34) // resource not found
                    break;
                else
                    throw e;
            }
        }

        for (Status tweet : tweets) {
            try {
                if(map.get(tweet.getText()) == null) { //if not present in map
                    log.info("T,");

                    //cleaning
                    String content = tweet.getText();
                    String url = "http://twitter.com/" + tweet.getUser().getId() + "/status/" + tweet.getId();
                    String origContent = content;
                    content = new TwitterNormalizer().cleanTweet(content);

                    //identify tweet language
                    Result res = lp.detectLanguage(content,this.lang);

                    if (res.languageCode.equals(lang) && res.isReliable) {
                        log.info("\n");
                        //Add to db
                        FileSaver file = new FileSaver(content, lang, "twitter", url, String.valueOf(tweet.getId()));
                        String filename = file.getFileName();
                        Tweet t = new Tweet(content,origContent,lang,tweet.getUser().getScreenName(),null,"TwitterUser",url,String.valueOf(tweet.getId()),filename);
                        if(DAO.saveEntry(t))
                            file.save(logDb);
                        map.put(tweet.getText(),true); //unique tweets

                        counter++;
                        if(counter > 500){ //refresh map cache
                            map = new HashMap<String, Boolean>();
                            counter = 0;
                        }
                    }
                }
            } catch (Exception e) {
                    log.error(e);
            }
        }
    }

    public void setKey(Twitter key) {
        this.twitter = key;
    }
}

