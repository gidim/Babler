package edu.columbia.main;

import edu.columbia.main.configuration.BabelConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * A poor's man text file database.
 * This class manages the log of previously saved data and id generation for new data
 * Each languageCode has its own log
 */


public class LogDB implements Serializable{

    public static final String SCRAPING_FOLDER = BabelConfig.getPathToScrapingFolder()+"/";
    public static final String LOG_FILE = "/log2.txt";
    private HashMap<String,LogDBEntry> db;
    File dbFile;
    static Logger log = Logger.getLogger(LogDB.class);

    public LogDB(String lang){

       this.dbFile = new File(SCRAPING_FOLDER+lang+LOG_FILE);

        if(!dbFile.exists()){
            File dir = new File(SCRAPING_FOLDER +lang);
            if(!dir.exists() || !dir.isDirectory())
                dir.mkdir();
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                log.error(e);
            }
        }
       readDBFromFile();
   }

    public LogDB(String lang, String type){

        this.dbFile = new File(SCRAPING_FOLDER+lang+"/"+type+".txt");
        if(!dbFile.exists())
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                log.error(e);
            }

        readDBFromFile();
    }

    private void readDBFromFile() {

        File f = dbFile;
        db = new HashMap<String, LogDBEntry>();
        Scanner in = null;
        try {
            in = new Scanner(new FileReader(f));
            while(in.hasNextLine()) {
                String nextLine = in.nextLine();
                LogDBEntry entry = new LogDBEntry(nextLine);
                db.put(entry.getId(),entry);
            }
        }
        catch(IOException e) {
            log.error(e);
        }
        finally {
            try { in.close() ; } catch(Exception e) { /* ignore */ }
        }
    }

    @Deprecated
    public static void log(String id, String lang){
        try {
            File f = new File(SCRAPING_FOLDER+lang+LOG_FILE);
            FileUtils.writeStringToFile(f, "id: "+id+"\n",true);

        } catch (IOException e) {
            log.error(e);
        }

    }

    /**
     *
     * @param id the id of the post
     * @param url the location from which we got that post
     * @param lang the languageCode of the post
     */
    public static void logWithUrl(String id, String url, String lang){
        try {
            File f = new File(SCRAPING_FOLDER+lang+LOG_FILE);
            FileUtils.writeStringToFile(f, "id: " +id+ ", url:"+url+"\n",true);

        } catch (IOException e) {
            log.error(e);
        }

    }



    public void logWithUrlNonStatic(String id, String url, String lang){
        try {
            File f = dbFile;
            FileUtils.writeStringToFile(f, "id: " +id+ ", url:"+url+"\n",true);
            LogDBEntry entry = new LogDBEntry(id,url);
            db.put(entry.getId(),entry);

        } catch (IOException e) {
            log.error(e);
        }

    }


    /**
     * Checks if a post was already save
     * @param id the ID of the post
     * @param lang the languageCode of the post
     * @return true for new posts
     */

    public static boolean isNew(String id, String lang){

        File f = new File(SCRAPING_FOLDER+lang+LOG_FILE);
        if(f.exists())
            return !find(f,id);
        else {
            try {
                FileUtils.writeStringToFile(f, "id: 1,url:www.test.com\n");
                return true;
            } catch (IOException e) {
                log.error(e);
            }
        }
        return true;

    }


    /**
     * Generates a new ID by checking the last ID in the log, if the log contains non int ID's
     * the method will generate a new random ID.
     * @param lang The languageCode of the log
     * @return Last id in the log +1 OR a random int
     * @throws Exception if failed to get id
     */
    public static String getNewId(String lang) throws Exception {
        File f = new File(SCRAPING_FOLDER+lang+LOG_FILE);
        String id = "";

        if(!f.exists()){
            FileUtils.writeStringToFile(f,"id: 1,url:www.test.com\n");
        }

        try {
            ReversedLinesFileReader fr = new ReversedLinesFileReader(f);

            String lastLine = fr.readLine();
            int commaLocation;
            try {
                 commaLocation = lastLine.indexOf(",");
            }
            catch (Exception ex){
                lastLine = fr.readLine();
                commaLocation = lastLine.indexOf(",");
            }
            id = lastLine.substring(4, commaLocation);

            if (id == "0") {
                throw new Exception("Couldn't get ID");
            }

        } catch (IOException e) {
            log.error(e);
        }

        try{
            int numId = Integer.parseInt(id);
            id = String.valueOf(++numId);
            return id;
        }
        catch(Exception exx){
            Random rand = new Random();
            id = String.valueOf(rand.nextInt()%10000);
            return id;
        }

    }

    /**
     * Search a file for a searchString
     * @param f the file to search in
     * @param searchString
     * @return true if found
     */
    public static boolean find(File f, String searchString) {
        boolean result = false;
        Scanner in = null;
        try {
            in = new Scanner(new FileReader(f));
            while(in.hasNextLine() && !result) {
                String nextLine = in.nextLine();
                result = nextLine.indexOf(searchString) >= 0;
            }
        }
        catch(IOException e) {
            log.error(e);
        }
        finally {
            try { in.close() ; } catch(Exception e) { /* ignore */ }
        }
        return result;
    }

    public boolean isNew(String id){
        return !db.containsKey(id);
    }

}
