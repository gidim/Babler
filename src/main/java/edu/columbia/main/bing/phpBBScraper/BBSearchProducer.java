package edu.columbia.main.bing.phpBBScraper;

import edu.columbia.main.LogDB;
import edu.columbia.main.LanguageDataManager;
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
public class BBSearchProducer extends BabelProducer {

    static AtomicInteger numOfRequests;
    Logger log = Logger.getLogger(BBSearchProducer.class);
    int ngram = BabelConfig.getInstance().getConfigFromFile().ngram();

    public BBSearchProducer(BabelBroker broker, String language) {
        this.broker = broker;
        this.lang = language;
        this.words = LanguageDataManager.getMostCommonWords(this.lang, 3000, ngram);
        this.logDb =  new LogDB(this.lang);
        numOfRequests = new AtomicInteger();
    }

    @Override
    protected void searchWordAndSave(String word){

        boolean breakFlag = false;
        AzureSearchWebQuery aq = new AzureSearchWebQuery();
        aq.setAppid(BabelConfig.getInstance().getConfigFromFile().bing());
        aq.setQuery(word + " AND \"Powered by phpBB\"" + " NOT lang:en");
        // The results are paged. You can get 50 results per page max.
        // This example gets 150 results
        aq.setPerPage(50);

        for (int i=1; !breakFlag ; i++) {
            aq.setPage(i);
            aq.doQuery();

            AzureSearchResultSet<AzureSearchWebResult> ars = aq.getQueryResult();
            numOfRequests.getAndIncrement();
            for (AzureSearchWebResult anr : ars) {
                BBJob job = new BBJob(anr.getUrl(),lang, logDb);
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