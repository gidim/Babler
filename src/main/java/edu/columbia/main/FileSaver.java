package edu.columbia.main;

import edu.columbia.main.configuration.BabelConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Gideon on 9/21/14.
 * Manages all file saving operations
 */
public class FileSaver {

    private  String hash;
    /** post it */
    String id;
    /** data of the post */
    String content;
    /** languageCode of the post */
    String language;
    /** the source of the post  - blog name*/
    String source;
    /** the url of the post */
    String url;
    Logger log = Logger.getLogger(FileSaver.class);

    /**
     * Constructur
     *
     * @param content
     * @param language
     * @param source
     * @param url
     * @param id
     */
    public FileSaver(String content, String language, String source, String url, String id) {

        this.id = id;
        this.content = content;
        this.language = language;
        this.source = source;
        this.url = url;

    }
    public FileSaver(String content, String language, String source, String url, String id, String hash) {
        this(content, language, source, url, id);
        this.hash = hash;

    }

    /**
     * Constructor that gets a new id from the DB
     * @param content
     * @param language
     * @param source
     * @param url
     */
    public FileSaver(String content, String language, String source, String url) {

            try {
                this.id = LogDB.getNewId(language);
            } catch (Exception e) {
                log.error(e);
                System.exit(0);
            }
        this.content = content;
        this.language = language;
        this.source = source;
        this.url = url;

    }



    public String getFileName(){
        String filename;
        if(hash != null && !hash.isEmpty())
            filename = BabelConfig.getPathToScrapingFolder()+"/"+language+"/"+source+"/"+ hash +".txt".replace("/","");
        else {
            filename = BabelConfig.getPathToScrapingFolder()+"/" + language + "/" + source + "/" + id + ".txt".replace("/", "");
            hash = "";
        }

        return filename;
    }

    /**
     * saves the file according to a specific directory structure
     * scraping/languageCode/source/id.txt
     * logs the saved file to the DB
     */
    public String save(LogDB logDb){
        try {
            String filename  = getFileName();
            File f = new File(filename);
            if(f.exists()) {
                log.warn(filename + " Already exists");
                return f.getName();
            }

            FileUtils.writeStringToFile(f, content, "UTF-8");


            LogDB.logWithUrl(id + "#" + hash, url, language);


            if(logDb != null){
                logDb.logWithUrlNonStatic(id+"#"+hash,url,language);
            }
            return f.getName();


        } catch (FileNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }

        return null;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
