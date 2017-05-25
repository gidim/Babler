package edu.columbia.main.collection;

import edu.columbia.main.LogDB;
import edu.columbia.main.LanguageDataManager;
import edu.columbia.main.configuration.BabelConfig;
import edu.columbia.main.db.DAO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Gideon on 4/8/15.
 */
public class BabelProducer implements Runnable{

    protected HttpClient httpClient;
    protected LogDB logDb;
    protected LogDB usersLogDB;
    protected BabelBroker broker;
    protected String lang;
    protected ArrayList<String> words;
    protected Set<String> users;
    Logger log = Logger.getLogger(BabelProducer.class);
    int ngram = BabelConfig.getInstance().getConfigFromFile().ngram();

    protected boolean byUsers = false;

    public BabelProducer(BabelBroker broker, HttpClient httpClient, String language) {
        this.broker = broker;
        this.lang = language;
        this.httpClient = httpClient;
        this.words = LanguageDataManager.getMostCommonWords(this.lang, 3000, ngram);
        this.logDb =  new LogDB(this.lang);
        this.usersLogDB = new LogDB(this.lang,"TopsyUsers");

    }

    public BabelProducer(BabelBroker broker, HttpClient httpClient, String language, boolean user) {
        this.broker = broker;
        this.lang = language;
        this.httpClient = httpClient;
        this.logDb =  new LogDB(this.lang);
        this.usersLogDB = new LogDB(this.lang,"TopsyUsers");
        this.byUsers = user;

        if(byUsers)
            //this.users = getUserIDsFromFile();
            this.users = getUsersFromDB();
        else
            this.words = LanguageDataManager.getMostCommonWords(this.lang, 3000, ngram);

    }

    public BabelProducer() {
    }

    @Override
    public void run() {
        try {
            if (byUsers) {
                scrapeByUser();
            } else {
                scrapeByMostCommonWords();
            }
        }
        catch (Exception ex){
            log.error(ex);
        }
    }

    private void scrapeByUser() {
        while(true) {
            Iterator it = users.iterator();
            while (it.hasNext()) {
                String user = (String) it.next();
                user = user.trim();
                searchUserAndSave(user);
                it.remove();
            }

            //after finishing all the words refill the list
            //this.users = getUserIDsFromFile();
            this.users = getUsersFromDB();
        }
    }



    public void scrapeByMostCommonWords() {

        while(true) {
            Iterator it = words.iterator();
            while (it.hasNext()) {
                String word = (String) it.next();
                word = word.trim();
                if (word.equals("") || word.contains("<NUM>") || word.contains("<num>") || word.length() < 3) {
                    continue;
                }
                searchWordAndSave(word);
                it.remove();
            }

            //after finishing all the words refill the list
            this.words = LanguageDataManager.getMostCommonWords(this.lang, 3000, ngram);
        }

    }

    private void searchUserAndSave(String user) {
        //get the JSON
        GetMethod get = null;
        BabelJob job = null;
        int i = 0;
        try {
            do {
                String query = "http://otter.topsy.com/search.js?q=from%3A"+user+"&type=tweet&offset="+i+"&perpage=100&window=a&apikey=09C43A9B270A470B8EB8F2946A9369F3";
                i += 100;
                //Thread.sleep(1000);
                get = new GetMethod(query);
                httpClient.executeMethod(get);
                String jsonData = get.getResponseBodyAsString();
                //System.out.println(jsonData);
                job = new BabelJob(jsonData, lang, logDb, usersLogDB);
                if(job.hasResulst()) {
                    broker.put(job);
                }
            }while(job.hasResulst());
        }
        catch (Exception ex){
            log.error(ex);
        }
        finally {
            get.releaseConnection();

        }
    }
    protected void searchWordAndSave(String word){
        //get the JSON
        GetMethod get = null;
        BabelJob job = null;
        int i = 0;
        try {
            do {

                String query = "http://otter.topsy.com/search.js?q="+ URLEncoder.encode(word,"UTF-8")+"&type=tweet&offset="+i+"&perpage=100&window=realtime&apikey=09C43A9B270A470B8EB8F2946A9369F3";
                // /"http://otter.topsy.com/search.js?callback=jQuery1830900925604859367_1428473097209&q=hey&offset="+i+"&perpage=100&window=h&sort_method=date&call_timestamp=1428473097553&apikey=09C43A9B270A470B8EB8F2946A9369F3&_=1428473108562"; //todo: implement query
                i += 100;
                //Thread.sleep(1000);
                get = new GetMethod(query);
                httpClient.executeMethod(get);
                String jsonData = get.getResponseBodyAsString();
                //System.out.println(jsonData);
                job = new BabelJob(jsonData, lang, logDb, usersLogDB);
                if(job.hasResulst()) {
                    broker.put(job);
                }
            }while(job.hasResulst());
        }
        catch (Exception ex){
            log.error(ex);
        }
        finally {
            get.releaseConnection();

        }
    }
    //Sania:
    //todo: replace with reading form database
    private Set<String> getUsersFromDB()
    {
        return DAO.getAllTwitterUserIds();
    }


//    private Set<String> getUserIDsFromFile() {
//        File f = new File("scraping/"+lang+"/TopsyUsers.txt");
//        Set<String> users = new HashSet<String>();
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new FileReader(f));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        String line;
//        try {
//            while ((line = br.readLine()) != null) {
//                int comma = line.indexOf(',');
//                String username = line.substring(4,comma);
//                users.add(username);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return users;
//    }

}
