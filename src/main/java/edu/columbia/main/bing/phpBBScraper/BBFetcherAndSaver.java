package edu.columbia.main.bing.phpBBScraper;

import edu.columbia.main.FileSaver;
import edu.columbia.main.db.DAO;
import edu.columbia.main.db.Models.ForumPost;
import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.language_id.Result;
import edu.columbia.main.collection.BabelConsumer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gideon on 4/24/15.
 */

/**
 * Fetches a job from the queue and tries to scrape the Forum posts from the job url
 */
public class BBFetcherAndSaver extends BabelConsumer implements Runnable{

    HttpClient httpClient;
    Logger log = Logger.getLogger(BBFetcherAndSaver.class);

    public BBFetcherAndSaver(BBBroker broker, LanguageDetector languageDetector, int i, HttpClient httpClient) {
        super(broker, languageDetector, i, null);
        this.httpClient = httpClient;
    }

    /**
     * pull job and run it
     */
    @Override
    public void run() {
        Thread.currentThread().setName("Parser " + i);

        BBJob data = null;
        try {

            while (true){
                data = (BBJob) broker.get();
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
     * Visits a forum page and tries to save all posts in that page.
     * After finishing with that page it tries to crawl to additional pages in that domain
     * @param job contains url to forum page
     */
    protected void searchAndSave(BBJob job){
        String url = job.getURL();
        int count = 5; // allow for non existing threads
        String threadNumber = job.getThreadNumber();
        while(count > 0){
            GetMethod get = null;

            try {
                //http://forum.asuultserver.com/viewtopic.php?f=102&t=190122&start=52
                String query = "http://"+ url + "&t="+threadNumber;
                //Thread.sleep(1000);
                get = new GetMethod(query);
                httpClient.executeMethod(get);
                String htmlData = get.getResponseBodyAsString();
                List<BBPost> posts  = null;
                try{posts  = parseThread(htmlData,job,threadNumber);}
                catch(Exception ex){
                    threadNumber = String.valueOf(Long.parseLong(threadNumber)+1); //++
                    continue;
                }

                if(posts == null || posts.size() == 0)
                    count--;

                else
                    count++;


                for(BBPost post : posts){
                    String content = post.getContent();
                    Result res = ld.detectLanguage(content,job.getLanguage());
                    if (res.languageCode.equals(job.getLanguage()) && res.isReliable) {
                        FileSaver file = new FileSaver(content, job.getLanguage(), "phpbb", url, String.valueOf(post.getId()), String.valueOf(post.getContent().hashCode()));
                        String filename = file.getFileName();
                        ForumPost forumPost = new ForumPost(content,job.getLanguage(),null,"phpbb",url,String.valueOf(post.getId()),filename);
                        if(DAO.saveEntry(forumPost))
                            file.save(job.getDB());
                    }
                    else{
                        count -=5;
                    }

                    if(count < 0)
                        break;
                }

                threadNumber = String.valueOf(Long.parseLong(threadNumber)+1); //++


            } catch (HttpException e) {
                log.error(e);
                count--;
            } catch (IOException e) {
                log.error(e);
                count--;
            } finally {
                get.releaseConnection();
            }


        }

    }


    /**
     * Given a thread page parse HTML and split to BBpost
     * @param htmlData raw html of page
     * @param job url of page
     * @param threadNumber an id used by the website to mark the thread
     * @return
     */
    private List<BBPost> parseThread(String htmlData, BBJob job, String threadNumber) {

        ArrayList<BBPost> posts = new ArrayList<BBPost>();

        Document doc = Jsoup.parse(htmlData);

        //System.out.println(html);
        //get the number of pages from the page's pagination
        Elements tables = doc.select(".tablebg");

        int i = 0;
        for(Element table: tables){
            String content = null;
            String id = null;

            Element temp  = table.select(".postbody").first();
            if(temp != null)
                content = temp.text();



            if(content != null) {
                posts.add(new BBPost(content, job.getForumNumber() + "/"+ threadNumber+"/"+i));
                i++;
            }

        }

        if(posts.size() == 0){ //div structure

            Elements pposts = doc.select(".inner");

            for(Element ppost : pposts){
                Element temp = ppost.select(".content").first();
                String content = null;
                String id = null;

                if(temp != null)
                    content = temp.text();


                if(content != null)
                    posts.add(new BBPost(content, job.getForumNumber() + "/"+ threadNumber+"/" +i));
                    i++;
            }



        }


        return posts;

    }


}
