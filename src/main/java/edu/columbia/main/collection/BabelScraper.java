package edu.columbia.main.collection;

import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.LanguageDataManager;
import edu.columbia.main.MTHttpClient;
import edu.columbia.main.normalization.TwitterNormalizer;
import org.apache.commons.httpclient.HttpClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Gideon on 4/8/15.
 */
public class BabelScraper {


    public static final int NUM_OF_CONSUMERS = 6; //todo:change
    public static final int NUM_OF_PRODUCERS = LanguageDataManager.getLanguages().length;
    boolean users = false;

    public BabelScraper(){}
    public BabelScraper(boolean users) {
        this.users = users;
    }

//    public void run(){
//
//        BabelBroker broker = new BabelBroker();
//        LanguageDetector lp = new LanguageDetector();
//        TwitterNormalizer normalizer = new TwitterNormalizer();
//        HttpClient httpClient = new MTHttpClient().getClient();
//        String[] langs = LanguageDataManager.getLanguages();
//
//
//
//        ExecutorService consumers = Executors.newFixedThreadPool(NUM_OF_CONSUMERS);
//        ExecutorService producers = Executors.newFixedThreadPool(NUM_OF_PRODUCERS); //one for each languageCode
//        Future [] producersFutures = new Future[NUM_OF_PRODUCERS];
//
//        //intiate producers
//        for(int i = 0 ; i < NUM_OF_PRODUCERS ; i++){
//            Future f = producers.submit(new BabelProducer(broker, httpClient,langs[i],users));
//            producersFutures[i] = f;
//        }
//
//        //initiate consumers
//        for(int i = 0 ; i < NUM_OF_CONSUMERS; i++){
//           consumers.execute(new BabelConsumer(broker, lp,i, normalizer));
//        }
//
//
//        try {
//            //wait for all producers
//            for(int i = 0 ; i < NUM_OF_PRODUCERS ; i++){
//                producersFutures[i].get();
//            }
//            producers.shutdown();
//            consumers.shutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//    }
}


