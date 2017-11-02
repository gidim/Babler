package edu.columbia.main;


import org.apache.log4j.Logger;
import edu.columbia.main.language_id.LanguageCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import edu.columbia.main.LogDB;

class URL{


    public String url;
    public String language;
    public String title;

    URL(String url, String language, String title) {
        this.url = url;
        this.language=language;
        this.title = title;
    }
}

/**
 * Fetches all the transcripts in a specific languageCode from TED.COM
 */
public class TEDScraper {

    Logger log = Logger.getLogger(TEDScraper.class);

    /** location of transcripts */
    private static final String VIDEOS_URL = "https://www.ted.com/talks/browse?language=";
    /** container for all the transcripts urls of a specific languageCode */
    private ArrayList<URL> urls;
    /** the destination langauge */
    private String language;
    private LogDB logDb;
    
    /**
     * Parses all the pages containing links to talks in a specific languageCode
     * saves it to urls and then calls getAndSaveData()
     * @param language destination languageCode
     */
    public TEDScraper(String language) {
        this.language = language;
        String iso1Lang = LanguageCode.convertIso2toIso1(language);
        this.logDb = new LogDB(this.language); //saving text files

        urls = new ArrayList<URL>(2);


            log.info("Scraping TED.COM for subtitles in:  "+ language);

            //get the first page and parse
            HTTPClient client = new HTTPClient(VIDEOS_URL + iso1Lang);
            String html = client.getHTMLData();

            if(html.contains("We couldn't find a talk quite like that")){
                log.info("TED.COM Does not have any talks in "+language + " langauge code");
                log.info("Stopping process");
                return;
            }


            Document doc = Jsoup.parse(html);

            //get the number of pages from the page's pagination
            Element lastPagination = doc.select(".pagination__item").last();

            int numOfPages = 1;
            if(lastPagination != null)
                numOfPages = Integer.parseInt(lastPagination.text());


            //for every page of that languageCode
            for (int i = 1; i <= numOfPages; i++) {
                log.info("Getting links from page: "+i +" out of: "+numOfPages);

                //we already fetched the first page
                if (i != 1) {
                    //get the page and parse
                    client = new HTTPClient(VIDEOS_URL + iso1Lang + "&page=" + i);
                    html = client.getHTMLData();
                    doc = Jsoup.parse(html);
                }

                Elements videoContainers = doc.select(".media__message");
                Elements links = videoContainers.select("a");

                //add href value only to urls
                for (Element link : links) {
                    //get the href value
                    String modifiedLink = link.attr("href");
                    //remove everything after the ? -> /talks/ze_frank_are_you_human?languageCode=lt
                    modifiedLink = modifiedLink.substring(0, modifiedLink.indexOf("?"));
                    //add to array of all links
                    urls.add(new URL("https://www.ted.com" + modifiedLink + "/transcript.json?language=" + iso1Lang, language,modifiedLink.substring((modifiedLink.indexOf("/talks/")+"/talks/".length()),modifiedLink.length())));
		    //urls.add(new URL("https://www.ted.com" + modifiedLink + "/transcript.json?language=en", "eng", modifiedLink.substring((modifiedLink.indexOf("/talks/")+"/talks/".length()),modifiedLink.length())));
                }

            }

        getAndSaveData();
    }

    /**
     *  loads each url from url, fetches the transcript, parses it and saves it.
     */
    public void getAndSaveData() {

        String content = "";

        for(URL url : urls){

            HTTPClient client = new HTTPClient(url.url);
            String html = client.getHTMLData();
            FileSaver file = new FileSaver(html, url.language, "TED", url.url, url.title + ".json");
	    file.save(logDb);
        }
    }


}
