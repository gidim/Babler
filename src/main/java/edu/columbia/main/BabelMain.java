package edu.columbia.main;

import edu.columbia.main.article_extraction.PostExtractor;

import edu.columbia.main.bing.blogspot_scraper.BSJobManager;
import edu.columbia.main.bing.phpBBScraper.BBJobManager;
import edu.columbia.main.configuration.BabelConfig;
import edu.columbia.main.db.ExportToText;
import edu.columbia.main.language_id.LanguageCode;
import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.language_id.Result;
import edu.columbia.main.language_id.cld.*;
import edu.columbia.main.language_id.lingpipe.*;
import edu.columbia.main.language_id.textcat.*;
import edu.columbia.main.twitter.TwitterCodeSwitchSJobManager;
import edu.columbia.main.twitter.TwitterJobManager;
import edu.columbia.main.twitter.TwitterJobManagerUser;
import edu.columbia.main.YouTube.YouTubeCaptionsScraper;
import edu.columbia.main.twitter.TwitterSentimentJobManager;
import org.apache.log4j.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;


/**
 * This tool scrapes the internet for conversational data in a specific languageCode.
 * The data is being checked to make sure it is in the desired languageCode using Google's compact languageCode detector
 * The code will not save duplicates by using a log of everything that was saved previously at :
 * scraping/languageCode/log2.txt
 *
 *  All the data will be saved following this directory structure:
 *  scraping/languageCode/source/id.txt
 *
 *  The script is tolerant for errors, either with failed HTTP requests or problems in parsing RSS feeds,
 *  please ignore the warnings.
 *
 *  The source code of a few custom scrapers built for a specific website is available in the edu.columbia.main.CustomScraper package
 *  the code code will only work for these website.
 *
 * DEPENDENCIES:
 * All the java dependencies are available in the lib folder.
 * running the ant build script will include all of them to one single jar
 * Additionally the following dependencies must be installed manually:
 *
 * automake-1.14.1
 * libtool 2.4.2
 * cld 1.0  - https://github.com/mzsanford/cld
 *
 *  BUILDING FROM SOURCE: from the main directory
 *  $ ant
 *
 *  RUNNING JAR FILE:
 *  ex: java -Dfile.encoding=UTF-8 -jar Babel.jar he -bs
 *  this will search blogspot blogs for posts in Hebrew
 *
 */

public class BabelMain {

    @Option(name="-l", aliases = "--lang", usage="Sets the data collection language in ISO_639_2 format")
    private String langCode = BabelConfig.getInstance().getConfigFromFile().lang();

    @Option(name="-sd", aliases = "--seedsFolder", usage="Path to seed folder containing files in the format of LANGCODEMostCommon.txt")
    private String langDataFolder = BabelConfig.getInstance().getConfigFromFile().lang();

    @Option(name = "-lgs", aliases = "--langs", handler = StringArrayOptionHandler.class, required = false)
    private String[] langs;

    @Option(name="-m", aliases = "--module", required=true, usage="Chooses the collection module out of 'ted' 'bs' 'wiki' 'twitter', 'twitterUsers', 'bb, 'diffbot','youtube'")
    private static String module = BabelConfig.getInstance().getConfigFromFile().module();

    @Option(name="-ex", aliases = "--export", usage="Path to Export a single text file with all the data in the DB")
    private static String export;

    @Option(name="-c", aliases = "--config", usage="Sets the path to config file, if not provided loads default")
    private static String pathConfig;

    @Option(name="-d", aliases = "--debug", usage="Sets choice to debug")
    private static Boolean debug = BabelConfig.getInstance().getConfigFromFile().debug();

    @Option(name="-wl", aliases = "--wordlist", usage="Path to specialized word list")
    private static String wordList;

    @Option(name="-ng", aliases = "--ngram", usage="Sets choice of which ngram model to run (1-unigram, 2-bigram, 3-trigram")
    private static int ngram = BabelConfig.getInstance().getConfigFromFile().ngram();



    static Logger log = Logger.getLogger(BabelMain.class);


    public void callMain(String args[]) throws Exception {

        setSystemSettings();
        CmdLineParser parser = new CmdLineParser(this);
        TwitterScraperFactory twitterFactory = new TwitterScraperFactory();

        try {
            // parse the arguments.
            parser.parseArgument(args);}
        catch(CmdLineException e ) {

            log.error(e.getMessage());
        }

        /**
         * Run some debug code before starting
         */
        if (debug){
            testLanguageId();
        }
        /**
         * load config file from path given.
         * If not provided default would be loaded from src/main/java/edu.columbia.main/configuration/ConfigFromFile.properties
         */
        if(pathConfig!=null)
        {
            BabelConfig.InsteantiateWithConfigFile(pathConfig);
        }

        if(langs != null){
            BabelConfig.getInstance().setLanguages(langs);
        }


        if(langDataFolder != null){
            BabelConfig.getInstance().setPathToDataFolder(langDataFolder);
        }

        /* set language code so all modules could use it */
        if(langCode != null){
            BabelConfig.getInstance().setCollectionLanguage(langCode);
        }
        String language = langCode;




        if(wordList != null){
            BabelConfig.getInstance().setPathToWordsList(wordList);
        }


        /**
         * Collects blog from blogspot.com using Bing
         */
        if(module.equals("bs")) {
            new BSJobManager().run();
        }

        /**
         * Scrapes all the subtitles from ted.com
         */
        else if(module.equals("ted")) {
            //todo: add db model for ted

            TEDScraper ts = new TEDScraper(langCode);
        }


        /**
         * Downloads tweets from the Twitter API
         */
        else if(module.equals("twitter")){

            TwitterJobManager tjm= new TwitterJobManager();
            tjm.start();
        }

        else if(module.equals("twitterSentiment")){

            twitterFactory.getTwitterScraper("TwitterSentiment");
        }
        else if(module.equals("twitterCodeSwitch")){

            twitterFactory.getTwitterScraper("TwitterCodeSwitch");
        }

        /**
         * Download tweets from the twitter API based on already saved tweets in the DB
         * For every user's tweet in the DB fetches all that user's tweets.
         */
        else if(module.equals("twitterUsers")){
            twitterFactory.getTwitterScraper("TwitterUsers");
        }

        /**
         * Collects data from web forums based on phpBB. Based on Bing
         */
        else if(module.equals("bb")){
            new BBJobManager.run();

        }

        /**
         * For every blog post in DB, revisit the post and tries to fetch paragraphs
         * that were missing before (due to RSS collection)
         */
        else if(module.equals("diffbot")){
            PostExtractor pex = new PostExtractor();
            pex.run();
        }

        /**
         * Downloads user generated captions from youtube
         */
        else if(module.equals("youtube")){
            YouTubeCaptionsScraper yt = new YouTubeCaptionsScraper(language);
            yt.scrape();
        }



        /************************************
         * Utils
         *************************************/


        /**
         * Creates a database dump
         */
        else if(export !=null){
            ExportToText.saveAllLanguageDataToFile(language, export);
        }


        log.info("------- FINISHED callMain() ------ ");
    }

    public static void main(String[] args) throws Exception {

        new BabelMain().callMain(args);

    }

    private static void testLanguageId() throws Exception {
        LanguageDetector ld = new LanguageDetector();
        LingPipe lingPipe = new LingPipe("completeModel3.gm");
        TextCategorizer tc = new TextCategorizer();
        Cld2 cld2;

        boolean local = false;

        //If running locally clean all cld errors
        try {
            cld2 = new Cld2();
        }
        catch (Exception ex){
            local = true;
        }
        catch (Error e){
            local = true;
        }

        String english = "This is a sentence in english, all classifiers should agree";
        String code = LanguageCode.convertLanguageNameToCode("English").getLanguageCode();
        Result res = ld.detectMajorityVote(english, code);
        if(!res.languageCode.equals(code)){
            log.error("majority vote failed");
            System.exit(0);
        }

        english = "This is a sentence in english, cld should respond";
        code = LanguageCode.convertLanguageNameToCode("English").getLanguageCode();
        res = ld.detectHierarchy(english, code);

        if(!res.languageCode.equals(code)){
            log.error("Hierarchy failed");
            System.exit(0);
        }
    }

    private static void setSystemSettings() {

        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(java.util.logging.Level.OFF);

        Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        Logger.getLogger("httpclient").setLevel(Level.OFF);
        Logger.getLogger("org.apache.http").setLevel(Level.OFF);

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");

        ConsoleAppender console = new ConsoleAppender(); //create appender
        //configure the appender
        String PATTERN = "%-5p : %d : %c - \"%m\"%n";
        console.setLayout(new PatternLayout(PATTERN));
        String console_level = BabelConfig.getInstance().getConfigFromFile().console_log_level();
        if(console_level.equalsIgnoreCase("ERROR"))
            console.setThreshold(Level.ERROR);
        else if(console_level.equalsIgnoreCase("FATAL"))
            console.setThreshold(Level.FATAL);
        else if(console_level.equalsIgnoreCase("WARN"))
            console.setThreshold(Level.WARN);
        else if(console_level.equalsIgnoreCase("INFO"))
            console.setThreshold(Level.INFO);
        else
            console.setThreshold(Level.DEBUG);
        console.activateOptions();
        //add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(console);

        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        fa.setFile(BabelConfig.getInstance().getConfigFromFile().logs_path());
        fa.setLayout(new PatternLayout(PATTERN));
        String file_level = BabelConfig.getInstance().getConfigFromFile().file_log_level();
        if(file_level.equalsIgnoreCase("ERROR"))
            fa.setThreshold(Level.ERROR);
        else if(file_level.equalsIgnoreCase("FATAL"))
            fa.setThreshold(Level.FATAL);
        else if(file_level.equalsIgnoreCase("WARN"))
            fa.setThreshold(Level.WARN);
        else if(file_level.equalsIgnoreCase("INFO"))
            fa.setThreshold(Level.INFO);
        else
            fa.setThreshold(Level.DEBUG);
        fa.setAppend(true);
        fa.activateOptions();
        //add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(fa);



    }
}

