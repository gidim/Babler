package edu.columbia.main.bing.blogspot_scraper;

import edu.columbia.main.collection.BabelConsumer;
import edu.columbia.main.collection.RSSScraper;
import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.screen_logging.TaskLogger;
import edu.columbia.main.screen_logging.ViewManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.util.AbstractMap;

/**
 * Created by Gideon on 4/24/15.
 */


/**
 * Fetches a job from the queue and tries to scrape the RSS feed from that results
 */
public class BSFetcherAndSaver extends BabelConsumer implements Runnable{

    HttpClient httpClient;
    Logger log = Logger.getLogger(BSFetcherAndSaver.class);
    ViewManager viewManager;
    public BSFetcherAndSaver(BSBroker broker, LanguageDetector languageDetector, int i, HttpClient httpClient, ViewManager viewManager) {
        super(broker, languageDetector, i, null);
        this.httpClient = httpClient;
        this.viewManager = viewManager;
    }

    /**
     * Fetch results
     */
    @Override
    public void run() {
        Thread.currentThread().setName("Parser " + i);
        BSJob data = null;
        try {
            while (true){
                data = (BSJob) broker.get();
                if (data != null) {
                    searchAndSave(data);
                }
            }
        }
        catch (InterruptedException e) {
            log.error(e);
        }
    }

    /**
     * Scrape RSS feed
     * @param job contains url to RSS feed
     */
    protected void searchAndSave(BSJob job){
        try {
            RSSScraper rssScraper = new RSSScraper(job.getURL(),job.getLanguage(),job.getDB(), this.ld);
            AbstractMap.SimpleEntry <Integer,Integer> vals = rssScraper.fetchAndSave();
            TaskLogger taskLogger = this.viewManager.getLogger(job.getLanguage());
            taskLogger.addTodupsdCount(vals.getKey());
            taskLogger.addToNotInLang(vals.getValue());

        } catch (MalformedURLException e) {
            log.error(e);

        } catch (Exception ex){
            log.error(ex);
        }

    }


}
