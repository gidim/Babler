package edu.columbia.main.twitter;

import edu.columbia.main.FileSaver;
import edu.columbia.main.LanguageDataManager;
import edu.columbia.main.LogDB;
import edu.columbia.main.configuration.BabelConfig;
import edu.columbia.main.db.DAO;
import edu.columbia.main.db.Models.Tweet;
import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.language_id.Result;
import edu.columbia.main.normalization.TwitterNormalizer;
import org.apache.log4j.Logger;
import twitter4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Gideon on 30/09/2016.
 */
public class TwitterCodeSwitchScraper {

    private Twitter twitter;
    private int counter = 0;
    private int numOfRequests = 0;
    private HashMap<String,Boolean> map = new HashMap<String,Boolean>();
    private ArrayList<String> words;
    private LogDB logDb;
    private String lang;
    private LanguageDetector languageDetector;

    static Logger log = Logger.getLogger(TwitterScraper.class);
    static int ngram = BabelConfig.getInstance().getConfigFromFile().ngram();


    public TwitterCodeSwitchScraper(String language,LanguageDetector languageDetector){
        this.lang = language; //1 lang per scraper
        this.words = LanguageDataManager.getMostCommonWords(this.lang,5000, ngram);
        this.logDb =  new LogDB(this.lang); //saving text files
        this.languageDetector = languageDetector;
    }


    /**
     * Iterates over all seed words
     * @throws TwitterException
     */
    public void scrapeByLanguage() throws TwitterException {

        while(true) {
            Iterator it = words.iterator();
            while (it.hasNext()) {
                String word = (String) it.next();
                word = word.trim();
                if (word.equals("")) {
                    continue;
                }
                searchAndSave(word, languageDetector, this.lang);
                it.remove();
            }

            //after finishing all the words refill the list
            this.words = LanguageDataManager.getMostCommonWords(this.lang, 5000, ngram);
        }

    }


    /**
     * Queries the twitter api for a given words and saves all matched results
     * @param word to query
     * @param lang that tweets should be in
     * @throws TwitterException
     */
    private void searchAndSave(String word, LanguageDetector lp, String lang) throws TwitterException {
        log.info("Searching for posts that contain the word: " + word);
        Query query = new Query("\""+word+"\"");
        // Since we're looking for code switching data we'll set the language code to be
        // in a different language then what the word is in.


        // This searches for english/spanish CS, for other pairs change following lines:
//        String firstLangArgument = BabelConfig.getInstance().getListOfLanguages()[0];
//        String secondLangArgument = BabelConfig.getInstance().getListOfLanguages()[1];

        if(lang.equals("en")){
            query.setLang("es");
        }
        else if (lang.equals("es")){
            query.setLang("en");
        }


        query.setCount(100);
        query.setSince("2010-01-01");
        QueryResult result;
        do {
            result = twitter.search(query);
            if(numOfRequests++ == 178){
                throw new TwitterException("API Limit");
            }
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                try {
                    if(map.get(tweet.getText()) == null) {
                        System.out.print("T,");
                        String content = tweet.getText();
                        String url = "http://twitter.com/" + tweet.getUser().getId() + "/status/" + tweet.getId();
                        String origContent = content;
                        content = new TwitterNormalizer().cleanTweet(content);
                        if (content.contains(word)) {
                            log.info("\n");
                            FileSaver file = new FileSaver(content, lang, "twitter_cs", url, String.valueOf(tweet.getId()));
                            String filename = file.getFileName();
                            Tweet t = new Tweet(content,origContent,this.lang,tweet.getUser().getScreenName(),null,"twitter_cs",url,String.valueOf(tweet.getId()),filename);
                            if(DAO.saveEntry(t))
                                file.save(logDb);
                            this.map.put(tweet.getText(),true); //to not repeat
                            counter++;
                            if(counter > 500){ //refresh map cache
                                this.map = new HashMap<String, Boolean>();
                                counter = 0;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        } while ((query = result.nextQuery()) != null);

    }



    public void setKey(Twitter key) {
        this.twitter = key;
    }



}
