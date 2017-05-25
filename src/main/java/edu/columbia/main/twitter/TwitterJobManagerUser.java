package edu.columbia.main.twitter;

import edu.columbia.main.LanguageDataManager;
import edu.columbia.main.Utils;
import edu.columbia.main.configuration.TwitterKeysConfiguration;
import edu.columbia.main.language_id.LanguageDetector;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sania on 2/18/16.
 */

/**
 * Manages twitter data collection by user
 * Each twitter user in the database is fetched and then this class tries to collect his
 * additional tweets
 */
public class TwitterJobManagerUser {
    //Creates a scarper for each language
    TwitterUsersScraper [] scrapers = new TwitterUsersScraper[LanguageDataManager.getLanguages().length];
    LanguageDetector languageDetector; //2 classifiers, sentence to lang
    TwitterKeysConfiguration tw = new TwitterKeysConfiguration();
    TwitterKey twitKey[] = tw.getByUserKeys();
    Twitter[] keys = new Twitter[twitKey.length]; //keys
    Logger log = Logger.getLogger(TwitterJobManagerUser.class);

    public TwitterJobManagerUser()
    {
        languageDetector = new LanguageDetector();

        for(int i =0; i< twitKey.length; i++)
        {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(twitKey[i].getConsumer())
                    .setOAuthConsumerSecret(twitKey[i].getSecret())
                    .setOAuthAccessToken(twitKey[i].getAccess())
                    .setOAuthAccessTokenSecret(twitKey[i].getToken_secret());

            TwitterFactory tf = new TwitterFactory(cb.build());
            keys[i] = tf.getInstance();

        }
    }

    public void start() throws InterruptedException {
        String[] langs = LanguageDataManager.getLanguages();
        long firstLimitHappendAt = 0l;
        int keyIndex = 0;

        for(int i=0 ; i < langs.length ; i ++){
            scrapers[i] = new TwitterUsersScraper(langs[i], languageDetector); //initializing scrapers for each lang
        }

        while(true) {
//Scraping for each language
            for (int i = 0 ; i < langs.length ; i++) {
                Twitter key = keys[keyIndex];
                TwitterUsersScraper ts = scrapers[i];
                ts.setKey(key);
                try {
                    log.info("Scraping twitter by user for languageCode: " + langs[i].toString());
                    ts.scrapeByLanguage(); //Calling the scraping function
                } catch (TwitterException e) { // we hit API limits

                    log.error(e);

                    if(keyIndex == 0){ // first time we hit the API limit
                        firstLimitHappendAt = System.currentTimeMillis();
                    }
                    else if(keyIndex == keys.length -1){
                        if(firstLimitHappendAt != -1) {
                            long timeToSleep = 900000 - (System.currentTimeMillis() - firstLimitHappendAt); //sleep 15 minutes since the limit hit
                            if(timeToSleep < 0){
                                timeToSleep = 900000;
                            }
                            log.info("going to sleep for " + TimeUnit.MILLISECONDS.toMinutes(timeToSleep) + " minutes");
                            Thread.sleep(timeToSleep);
                            //good morning
                            firstLimitHappendAt = -1;
                            keyIndex = -1;
                            continue;
                        }
                    }
                }
                finally {
                    keyIndex++;
                    if (keyIndex == keys.length)//wraparound
                        keyIndex = 0;
                }
            }
        }

    }

}
