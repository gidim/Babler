/**
 * Created by Sania on 2/2/16.
 */

import com.mongodb.BasicDBObject;
import edu.columbia.main.configuration.BabelConfig;
import edu.columbia.main.configuration.ConfigFromFile;
import edu.columbia.main.db.Models.BlogPost;
import edu.columbia.main.twitter.TwitterKey;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static com.mongodb.client.model.Filters.*;


public class TestConfigFromFile {

    @Test
    public void testCreateConfigFromFile() {
        //create fake properties file and put in test/resources
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("configFromFile.properties").getFile());
        BabelConfig.InsteantiateWithConfigFile(file);
        ConfigFromFile cfg = BabelConfig.getInstance().getConfigFromFile();
        //check the configuration methods does not return null
        assertNotNull(cfg.twitter_consumer());
        assertNotNull(cfg.twitter_access());
        assertNotNull(cfg.twitter_secret());
        assertNotNull(cfg.twitter_token_secret());
        assertNotNull(cfg.diffbot());
        assertNotNull(cfg.bing());
        assertNotNull(cfg.google_cse_cx());
        assertNotNull(cfg.google_cse_key());
        assertNotNull(cfg.lang());
        assertNotNull(cfg.module());
        assertNotNull(cfg.debug());
        assertNotNull(cfg.console_log_level());
        assertNotNull(cfg.file_log_level());
        assertNotNull(cfg.logs_path());
        assertNotNull(cfg.ngram());
    }

    @Test
    public void testCreateConfigWithoutFile() {
        String p =null;
        BabelConfig.InsteantiateWithConfigFile(p);
        ConfigFromFile cfg = BabelConfig.getInstance().getConfigFromFile();
        //check the configuration methods does not return null
        assertNotNull(cfg.twitter_consumer());
        assertNotNull(cfg.twitter_access());
        assertNotNull(cfg.twitter_secret());
        assertNotNull(cfg.twitter_token_secret());
        assertNotNull(cfg.diffbot());
        assertNotNull(cfg.bing());
        assertNotNull(cfg.google_cse_cx());
        assertNotNull(cfg.google_cse_key());
        assertNotNull(cfg.lang());
        assertNotNull(cfg.module());
        assertNotNull(cfg.debug());
        assertNotNull(cfg.console_log_level());
        assertNotNull(cfg.file_log_level());
        assertNotNull(cfg.logs_path());
        assertNotNull(cfg.ngram());
    }

}