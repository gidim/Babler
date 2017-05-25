package edu.columbia.main.YouTube;

import edu.columbia.main.*;
import edu.columbia.main.configuration.BabelConfig;
import edu.columbia.main.google.GoogleCSE;
import edu.columbia.main.language_id.LanguageCode;
import edu.columbia.main.language_id.LanguageDetector;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gideon on 2/17/16.
 */

/**
 * Collects youtube video captions in a given language
 */
public class YouTubeCaptionsScraper {


    private final String lang;
    private final HttpClient client;
    private List words;
    private LogDB logDb;
    private final LanguageDetector lp = null;
    private final GoogleCSE googleSearch;
    private final String captionsIndex = "http://video.google.com/timedtext?type=list&v=";
    private final String captionEndPoint = "http://video.google.com/timedtext?";
    Logger log = Logger.getLogger(YouTubeCaptionsScraper.class);
    int ngram = BabelConfig.getInstance().getConfigFromFile().ngram();

    public YouTubeCaptionsScraper(String lang){
        this.lang = lang;
        this.words = LanguageDataManager.getMostCommonWords(this.lang, 3000, ngram);
        this.logDb =  new LogDB(this.lang);
        //this.lp = new LanguageDetector();
        this.googleSearch = new GoogleCSE();
        client = new MTHttpClient().getClient();

    }


    /**
     * Iterates over all words in seed words
     */
    public void scrape() {
        while(true) {
            Iterator it = words.iterator();
            while (it.hasNext()) {
                String word = (String) it.next();
                word = word.trim();
                if (word.equals("")) {
                    continue;
                }
                try {
                    searchAndSave(word);
                    it.remove();
                } catch (Exception e) {
                    log.error(e);
                    log.error("Going to sleep till tomorrow");
                    try {
                        TimeUnit.HOURS.sleep(24);
                    } catch (InterruptedException e1) {
                        log.error(e1);
                    }

                }
            }
            this.words = LanguageDataManager.getMostCommonWords(this.lang, 3000, ngram);
        }

    }

    /**
     * Queries Google CSE for a word and iterate over results
     * @param word
     * @throws Exception
     */
    private void searchAndSave(String word) throws Exception {

        log.info("Searching for word " + word);

        List<com.google.api.services.customsearch.model.Result> results  = googleSearch.search(word);
        for (com.google.api.services.customsearch.model.Result res : results){

            String title = res.getTitle();
            String url = res.getFormattedUrl();

            if(url.contains("/user/") || url.contains("/channel/"))
                continue;

            String videoID = url.substring(url.indexOf("watch?v=")+"watch?v=".length());
            log.info("Checking video:" + videoID+",");

            if(videoHasCaptionsInLanguage(videoID,lang)){
                getAndSaveTranscript(videoID,lang);
            }


        }


    }

    /**
     * Fetches captions/transcript for a given video
     * @param videoID to fetch
     * @param lang this captions should be in
     * @throws IOException
     */
    public void getAndSaveTranscript(String videoID, String lang) throws IOException {

        lang = LanguageCode.convertIso2toIso1(lang);

        String url = captionEndPoint+"lang="+lang+"&v="+videoID;
        GetMethod get = new GetMethod(url);
        this.client.executeMethod(get);
        String xmlData = get.getResponseBodyAsString();

        //parse XML
        Document doc = Jsoup.parse(xmlData, "", Parser.xmlParser());
        String allCaps = "";
        for (Element e : doc.select("text")) {
            allCaps += e.text();
        }

        FileSaver file = new FileSaver(allCaps, lang, "youtube_caps", url, videoID);
        file.save(logDb);

    }

    /**
     * Checks if a given video has captions in our target language. As identified by the user who entered them
     * @param videoID to check
     * @param lang target
     * @return true if there are captions in lang
     * @throws IOException
     */
    public boolean videoHasCaptionsInLanguage(String videoID, String lang) throws IOException {
        //visit captions index
        GetMethod get = new GetMethod(captionsIndex+videoID);
        this.client.executeMethod(get);
        String xmlData = get.getResponseBodyAsString();

        //parse XML
        Document doc = Jsoup.parse(xmlData, "", Parser.xmlParser());

        //iterate over all captions
        for (Element e : doc.select("track")) {
            String langCode = e.attr("lang_code");
            String fixedLangCode = LanguageCode.convertIso1toIso2(langCode);
            if(fixedLangCode.equals(lang))
                return true;
        }

        return false;
    }


}