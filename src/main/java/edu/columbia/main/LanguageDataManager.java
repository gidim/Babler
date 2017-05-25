package edu.columbia.main;

import edu.columbia.main.configuration.BabelConfig;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gideon on 7/31/15.
 */

/**
 * A singelton that hold the list of languages we're collection data for
 * and their respective seed words
 */

public enum LanguageDataManager {
    INSTANCE;

    static Logger log = Logger.getLogger(LanguageDataManager.class);

    /* path to resources folder with seed word files */
    public static final String LANGUAGE_DATA_FOLDER = "languageData/";



    public static ArrayList<String> readFile(String path){
        if(path != null){
            File fw = new File(path);
            //read the words from the path
            ArrayList<String> lst = null;
            try {
                lst = (ArrayList) FileUtils.readLines(fw,"UTF-8");
            } catch (IOException e) {
                log.debug(e);
            }
            //return as list
            return lst;
        }
        return null;
    }

    public static ArrayList<String> getMostCommonWords(String lang, int n, int ngram) {

        String path = BabelConfig.getInstance().getPathToWordsList();
        ArrayList<String> seedQueries = null;

        if(path != null){
            seedQueries = readFile(path);
            if(seedQueries != null)
                return seedQueries;
        }


        //if MostCommon file exists in user's data folder, load from there
        String userPath = BabelConfig.getInstance().getPathToDataFolder();
        seedQueries = readFile(userPath + lang);
        if(seedQueries != null)
            return seedQueries;


        //If mostcommon file exists in resources, load it from file
        InputStream f = BabelConfig.getResourceAsStream(LANGUAGE_DATA_FOLDER + lang);
        if(f != null){
            seedQueries =  getMostCommonWordsFromFile(f,n);
        }

        //No data found
        if(seedQueries == null || seedQueries.isEmpty()) {
            log.error("Could not locate any word list for seeding. Shutting down");
            System.exit(0);
        }
        return null;
    }


    /**
     * loads most common words from file
     * @param f stream to load from
     * @param n number of words to load
     * @return
     */
    public static ArrayList<String> getMostCommonWordsFromFile(InputStream f, int n) {

        ArrayList<String> words = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(f, StandardCharsets.UTF_8));
        int counter = 0;
        String line;
        try {
            while ((line = br.readLine()) != null && counter <= n) {
                words.add(line);
            }
        } catch (IOException e) {
            log.error(e);
        }

        return words;
    }
    static class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;
        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }


    public static String[] getLanguages() {
        return BabelConfig.getInstance().getListOfLanguages();
    }


}
