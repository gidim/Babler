package edu.columbia.main.configuration;

import org.aeonbits.owner.ConfigFactory;
import org.apache.log4j.Logger;

import javax.security.auth.login.Configuration;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by Gideon on 9/29/15.
 */

/**
 * A singelton class to fetch all global configuration parameters
 */
public class BabelConfig {
    private static Logger log = Logger.getLogger(BabelConfig.class);
    private static volatile BabelConfig instance = null;
    private ConfigFromFile cfg;
    private String[] collectionLanguage;
    private String pathToWordsList;
    private String pathToDataFolder;

    /**
     * Create config object from file
     *
     * @param f file descriptor for config file
     */
    private BabelConfig(File f) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(f));
            cfg = ConfigFactory.create(ConfigFromFile.class, props);
        } catch (IOException e) {
            cfg = ConfigFactory.create(ConfigFromFile.class);
        }
    }

    /**
     * Creates a config object from file in path or from default if fails to load file
     *
     * @param pathToConfig
     */
    private BabelConfig(String pathToConfig) {
        //if string null or empty, use or own configFromFile.properties.
        if (pathToConfig == null) {
            cfg = ConfigFactory.create(ConfigFromFile.class);
        } else {
            //get config.properties file from path

            try {
                Properties props = new Properties();
                props.load(new FileInputStream(new File(pathToConfig)));
                cfg = ConfigFactory.create(ConfigFromFile.class, props);
            } catch (IOException e) {
                cfg = ConfigFactory.create(ConfigFromFile.class);
            }

        }

    }

    /**
     * Wrapper for constructor since class is singelton
     *
     * @param pathToFile path to config file
     */
    public static void InsteantiateWithConfigFile(String pathToFile) {
        instance = new BabelConfig(pathToFile);
    }

    /**
     * Wrapper for constructor since class is singelton
     *
     * @param f file descriptor for config file
     */
    public static void InsteantiateWithConfigFile(File f) {
        instance = new BabelConfig(f);
    }

    /**
     * Fetch singlton object
     *
     * @return Instance of BabelConfig (this class)
     */
    public static BabelConfig getInstance() {
        String p = null;
        if (instance == null) {
            synchronized (BabelConfig.class) {
                if (instance == null)
                    instance = new BabelConfig(p);
            }
        }
        return instance;
    }

    /**
     * @return string with path to where scraping files are saved
     */
    public static String getPathToScrapingFolder() {
        ConfigFromFile cfg = BabelConfig.getInstance().getConfigFromFile();
        String remote = cfg.productionScarpingFolderPath();
        return remote;
    }

    /**
     * Loads a resource as stream from resources folder or temp folder
     */

    public ConfigFromFile getConfigFromFile() {

        return cfg;
    }

    /**
     * Loads a resource as stream from resources folder
     *
     * @param str path to resource
     * @return
     */
    public static InputStream getResourceAsStream(String str) {

        File f = new File(str);
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                return fs;
            } catch (FileNotFoundException e) {

            }
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(str);
    }

    /**
     * Returns path to local resource
     *
     * @param str local path
     * @return absolute path
     */
    public static String getResource(String str) {

        File f = new File(str);
        if (f.exists())
            return f.getAbsolutePath();


        try {
            return BabelConfig.class.getResource("/" + str).toURI().getPath();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    /**
     * @return absolute path to resources folder
     */
    public static String getResourcesPath() {
        try {
            return BabelConfig.class.getResource("/completeModel3.gm").toURI().getPath().replace("completeModel3.gm", "");
            //using completeModel3.gm as a hack to get the path to its directory
        } catch (URISyntaxException e) {
            log.error(e);
            return null;
        }
    }

    public String[] getListOfLanguages() {
        return collectionLanguage;
    }

    public void setCollectionLanguage(String langCode) {
        if (langCode != null && !langCode.isEmpty()) {
            collectionLanguage = new String[1];
            collectionLanguage[0] = langCode;
        }
    }

    public String getPathToWordsList() {
        if (pathToWordsList != null)
            return pathToWordsList;
        else
            return BabelConfig.getInstance().getConfigFromFile().pathToWordsFile();
    }

    public void setPathToWordsList(String path) {
        this.pathToWordsList = path;
    }

    public void setPathToDataFolder(String pathToDataFolder) {
        this.pathToDataFolder = pathToDataFolder;
    }

    public String getPathToDataFolder() {
        return this.pathToDataFolder;
    }

    public void setLanguages(String[] languages) {
        this.collectionLanguage = languages;
    }
}
