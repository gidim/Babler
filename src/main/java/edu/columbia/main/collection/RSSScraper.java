package edu.columbia.main.collection;

import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import edu.columbia.main.FileSaver;
import edu.columbia.main.LogDB;
import edu.columbia.main.db.DAO;
import edu.columbia.main.db.Models.BlogPost;
import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.language_id.Result;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;


/**
 * This class scrapes data from a RSS feed, checks that it is in the desired languageCode and finally saves it
 * Created by Gideon on 9/28/14.
 *
 */
public class RSSScraper {

    private final LanguageDetector ld;
    public String language = "";
    public int numOfFiles = 0;
    public int wrongCount = 0;
    private LogDB logDb;
    private String url;
    static Logger log = Logger.getLogger(RSSScraper.class);

    public RSSScraper(String url, String language, LogDB logDb, LanguageDetector ld) {
        this.url = url;
        this.language = language;
        this.logDb = logDb;
        this.ld = ld;

    }


    public AbstractMap.SimpleEntry<Integer, Integer> fetchAndSave() throws Exception {

        URL url = new URL(this.url);

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));


        int items = feed.getEntries().size();

        if(items > 0){
            log.info("Attempting to parse rss feed: "+ this.url );
            log.info("This Feed has "+items +" items");
        }

        List <SyndEntry> entries = feed.getEntries();

        for (SyndEntry item : entries){
            log.info("Title: " + item.getTitle());
            log.info("Link: " + item.getLink());
            SyndContentImpl contentHolder = (SyndContentImpl) item.getContents().get(0);
            String content = contentHolder.getValue();

            //content might contain html data, let's clean it up
            Document doc = Jsoup.parse(content);
            content = doc.text();
            try {
                    Result result = ld.detectLanguage(content, language);
                    if (result.languageCode.equals(language) && result.isReliable) {

                        FileSaver file = new FileSaver(content, this.language, "bs", item.getLink(), item.getUri(), String.valueOf(content.hashCode()));
                        String fileName = file.getFileName();
                        BlogPost post = new BlogPost(content,this.language,null,"bs",item.getLink(),item.getUri(),fileName);
                        if(DAO.saveEntry(post)) {
                            file.save(this.logDb);
                            numOfFiles++;
                            wrongCount = 0;
                        }

                    }

                    else{
                        log.info("Item " + item.getTitle() + "is in a diff languageCode, skipping this post  "+ result.languageCode);
                        wrongCount ++;
                        if(wrongCount > 3){
                            log.info("Already found 3 posts in the wrong languageCode, skipping this blog");
                        }
                        break;
                    }

            }
            catch(Exception e){
                log.error(e);
                break;
            }


        }
        return new AbstractMap.SimpleEntry<>(numOfFiles,wrongCount);
    }

    public static List getAllPostsFromFeed(String urlToGet, String source) throws IOException, FeedException {

        ArrayList<BlogPost> posts = new ArrayList<BlogPost>();

        URL url = new URL(urlToGet);
        SyndFeedInput input = new SyndFeedInput();
        try {
            SyndFeed feed = input.build(new XmlReader(url));

            int items = feed.getEntries().size();

            if (items > 0) {
                log.info("Attempting to parse rss feed: " + urlToGet);
                log.info("This Feed has " + items + " items");
                List<SyndEntry> entries = feed.getEntries();

                for (SyndEntry item : entries) {
                    if (item.getContents().size() > 0) {
                        SyndContentImpl contentHolder = (SyndContentImpl) item.getContents().get(0);
                        String content = contentHolder.getValue();
                        if (content != null && !content.isEmpty()) {
                            BlogPost post = new BlogPost(content, null, null, source, item.getLink(), item.getUri(), null);
                            posts.add(post);
                        }
                    }
                }
            }
            return posts;
        }
        catch(Exception ex){
            log.error(ex);
            return posts;
        }

    }

}