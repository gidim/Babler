package edu.columbia.main.article_extraction;
import com.diffbot.clients.DiffbotClient;
import com.mongodb.BasicDBObject;
import com.sun.syndication.io.FeedException;
import edu.columbia.main.*;
import edu.columbia.main.collection.RSSScraper;
import edu.columbia.main.configuration.BabelConfig;
import edu.columbia.main.db.DAO;
import edu.columbia.main.db.Models.BlogPost;
import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.language_id.Result;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Created by Gideon on 10/14/15.
 */


/**
 * This class implements a web collection system that uses DiffBot's content extraction API
 * Due to our blog collection system that's based on RSS we sometime don't have the full post in the DB (just a snippet).
 * This class searches for blogs in the DB that weren't optimized (haven't been visited by the class) and then revisits them
 * and collects the full post.
 * It will automatically work on languages that we have the least data on (to optimize the limited queries)
 *
 * An Diffbot api token is required! please obtain one at diffbot.com and put it in the config file
 * as
 * diffbot=token
 *
 */
public class PostExtractor {


    public static final int NUM_TO_FETCH = 500;
    DiffbotClient articlesClient;
    HashMap<String,Boolean> visitedFeed = new HashMap<>();
    LanguageDetector ld;
    Logger log = Logger.getLogger(PostExtractor.class);

    public PostExtractor(){
        String token= BabelConfig.getInstance().getConfigFromFile().diffbot();
        articlesClient = new DiffbotClient(token);
        ld = new LanguageDetector();
    }

    /**
     * gets the full content of a url, if fails tries again after 1 sec
     * @param url
     * @return
     * @throws IOException
     */
    public String getBlogPostFromURL(String url) throws IOException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e);
        }
        DiffbotArticle a = null;
        try{ a = (DiffbotArticle) articlesClient.getArticle(DiffbotArticle.class,url);}
        catch (Exception ex){
            try {
                log.info("Sleeping till diffBot allows us to continue. 1 sec.");
                Thread.sleep(1000);
                return getBlogPostFromURL(url);

            } catch (InterruptedException e) {
                log.error(e);
            }
        }
        return a.getText();
    }


    /**
     * - count number of posts for each languageCode that weren't optimized
     * - sort languages by that count
     * - for each languageCode:
     *      - query all posts that weren't optimized
     *             - for each post:
     *                      - replace content with new content
     *                      - log stats
     */

    public void run() throws IOException, FeedException {

        long counter = 0;

        //count number of posts for each languageCode that weren't optimized
        String [] languages = LanguageDataManager.getLanguages();
        ArrayList <LanguageWithPostCount> langs = new ArrayList<LanguageWithPostCount>();
        for(String lang : languages)
            langs.add(new LanguageWithPostCount(lang,DAO.countNonOpimizedBlogPostsInLanguage(lang)));

        //sort languages by that count
        Collections.sort(langs, new Comparator<LanguageWithPostCount>() {
            @Override
            public int compare(LanguageWithPostCount o1, LanguageWithPostCount o2) {
                return Long.compare(o1.count,o2.count);
            }
        });

        log.info(langs);


        //for each languageCode
        for(LanguageWithPostCount lang : langs){
            log.info("Starting collection for: "  +lang);
            if(lang.count == 0)
                continue;

            LogDB logDB = new LogDB(lang.language);

            ArrayList<BlogPost> postsFromDB = DAO.getNNonOpimizedBlogPostsInLanguage(lang.language, NUM_TO_FETCH, 0);
            Iterator it = postsFromDB.iterator();
            int i = NUM_TO_FETCH;
            while(true){
                if(!it.hasNext()){
                    postsFromDB = DAO.getNNonOpimizedBlogPostsInLanguage(lang.language, NUM_TO_FETCH, i);
                    if(postsFromDB.size() == 0)
                        break;
                    else {
                        it = postsFromDB.iterator();
                        i +=NUM_TO_FETCH;
                    }
                }

                //get blog from db
                BlogPost post =  new BlogPost((BasicDBObject) it.next());

                if(visitedFeed.containsKey(post.getUrl()))
                    continue;

                //get all posts in post feed
                List<BlogPost> allPostsInFeed = RSSScraper.getAllPostsFromFeed(makeRSSUrl(post.getUrl()), post.getSource());

                //if found anything new make sure to not visit that post's feed anymore
                if(allPostsInFeed.size() > 0 ) {
                    post.setOptimized(true);
                    DAO.replaceEntry(BlogPost.class, post, post);
                    visitedFeed.put(post.getUrl(),true);
                }
                for(BlogPost newFeedPost : allPostsInFeed){

                    //if we already have a truncated version of this post then delete it later
                    BlogPost deleteCandidate = null;
                    if(!DAO.isNew(newFeedPost)){
                        deleteCandidate = new BlogPost(newFeedPost);
                    }


                    //make sure we haven't fetched this new post already
                    //if(DAO.isNewByURL(BlogPost.class,newFeedPost.getUrl())){
                    if(true){
                        //set new data just collected into post
                        String newData = getBlogPostFromURL(newFeedPost.getUrl());
                        if(newData == null || newData.isEmpty())
                            continue;

                        log.info("Got " + (newData.length()) + " chars for post " + post.getUrl() + "|| " + counter);
                        newFeedPost.setOptimized(true);
                        newFeedPost.setData(newData);
                        newFeedPost.setSource(post.getSource());
                        Result res = ld.detectLanguage(newData,post.getLanguage());

                        //try to save new post and delete old one
                        if(res.languageCode.equals(lang.language)){
                            FileSaver file = new FileSaver(newData, lang.language, post.getSource(), newFeedPost.getUrl(), newFeedPost.getId(), String.valueOf(newData.hashCode()));
                            String fileName = file.getFileName();
                            newFeedPost.setFileName(fileName);
                            newFeedPost.setLanguage(lang.language);
                            if(DAO.saveEntry(newFeedPost))
                                file.save(logDB);
                            counter++;
                            if(deleteCandidate !=null)
                                DAO.deleteEntry(deleteCandidate);
                        }
                    }
                }

            }
        }

    }

    private String makeRSSUrl(String url) {

        if(url.contains("/feeds/posts/default"))
            return url;

        java.net.URL tempUrl = null;
        try {
            tempUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "http://" + tempUrl.getHost() + "/feeds/posts/default";
    }


}