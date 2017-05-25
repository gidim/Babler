package edu.columbia.main.twitter;

import edu.columbia.main.configuration.TwitterKeysConfiguration;
import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.LanguageDataManager;
import edu.columbia.main.Utils;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gideon on 3/31/15.
 */


/**
 * Manages tweets collection from the Twitter API
 */
public class TwitterSentimentJobManager {


    /* An api consumer for each language */
    ArrayList<TwitterSentimentScraper>  scrapers = new ArrayList<>();

    /* Language identification */
    LanguageDetector languageDetector;

    /* Twitter API keys */
    TwitterKeysConfiguration tw = new TwitterKeysConfiguration();

    TwitterKey twitKey[] = tw.getBySeedQueryKeys();
    Twitter [] keys = new Twitter[twitKey.length]; //keys

    Logger log = Logger.getLogger(TwitterJobManager.class);

    public TwitterSentimentJobManager()
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

    /**
     * Manages the TwitterScraper instances and API quota limitations.
     * Automatically sleeps after all API keys were exhausted
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        String[] langs = LanguageDataManager.getLanguages();

        long firstLimitHappendAt = 0l;
        int keyIndex = 0;

        for(int i=0 ; i < langs.length ; i++){
            scrapers.add(new TwitterSentimentScraper(langs[i], languageDetector, TwitterSentimentScraper.Sentiment.Neutral));
            scrapers.add(new TwitterSentimentScraper(langs[i], languageDetector, TwitterSentimentScraper.Sentiment.Positive));
            scrapers.add(new TwitterSentimentScraper(langs[i], languageDetector, TwitterSentimentScraper.Sentiment.Negative));
        }

        while(true) {
            for (int i = 0 ; i < langs.length*3 ; i++) {
                Twitter key = keys[keyIndex];
                TwitterSentimentScraper ts = scrapers.get(i);
                ts.setKey(key);
                try {
                    log.info("Scraping twitter for languageCode: " +ts.getLanguage().toUpperCase());

                    ts.scrapeByLanguage();
                    //test();
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
