package edu.columbia.main.bing.blogspot_scraper;

import edu.columbia.main.LanguageDataManager;
import edu.columbia.main.LogDB;
import edu.columbia.main.configuration.BabelConfig;
import edu.columbia.main.collection.BabelBroker;
import edu.columbia.main.collection.BabelProducer;
import net.billylieurance.azuresearch.AzureSearchResultSet;
import net.billylieurance.azuresearch.AzureSearchWebQuery;
import net.billylieurance.azuresearch.AzureSearchWebResult;
import org.apache.log4j.Logger;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Gideon on 4/24/15.
 */


/**
 * Searches bing API for a word and puts all results into broker
 */
public class BSSearchProducer extends BabelProducer {

    static AtomicInteger numOfRequests;
    Logger log = Logger.getLogger(BSSearchProducer.class);
    int ngram = BabelConfig.getInstance().getConfigFromFile().ngram();

    public BSSearchProducer(BabelBroker broker, String language) {
        this.broker = broker;
        this.lang = language;
        this.words = LanguageDataManager.getMostCommonWords(this.lang, 1000, ngram);
        this.logDb =  new LogDB(this.lang);
        numOfRequests = new AtomicInteger();
    }

    @Override
    protected void searchWordAndSave(String word){
        boolean breakFlag = false;
        int counter = 0;
        AzureSearchWebQuery aq = new AzureSearchWebQuery();
        aq.setAppid(BabelConfig.getInstance().getConfigFromFile().bing());//julia's key
        //aq.setAppid("8LcemWAvkBRUS/uVBsI0vQFDT74FfrgyiV+PTkPIjMw");
        aq.setQuery("site:blogspot.com " + " \""+word+"\"" + " NOT lang:en"); //site:blogspot.com "word"
        aq.setPerPage(50);
        // The results are paged. You can get 50 results per page max.
        for (int i=1; !breakFlag ; i++) {
            aq.setPage(i);
            aq.doQuery();
            AzureSearchResultSet<AzureSearchWebResult> ars = aq.getQueryResult();
            if(counter++ == 100 ||ars.getAsrs().size() == 0)
                breakFlag = true;
            numOfRequests.getAndIncrement();
            for (AzureSearchWebResult anr : ars) {
                BSJob job = new BSJob(anr.getUrl(),lang, logDb);
                try {
                    if(job.isValid())
                        broker.put(job);
                    else{
                        log.debug("Job not valid: " + job);
                    }
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
        }
    }



}