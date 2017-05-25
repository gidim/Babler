import com.mongodb.BasicDBObject;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import edu.columbia.main.db.DAO;
import edu.columbia.main.db.Models.Tweet;
import edu.columbia.main.db.MongoDB;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Gideon on 10/1/15.
 */
public class TestDAO {

    MongodExecutable mongodExecutable = null;

    @Before
    public void setUp() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        String bindIp = "localhost";
        int port = 27017;
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                .build();


        mongodExecutable = starter.prepare(mongodConfig);
        MongodProcess mongod = mongodExecutable.start();
    }

    @Test
    public void testInsert(){
        String data = "Исламын бүлэглэлүүд нь Узбек, Тажик, Киргиз улсыг хамарсан Ферганын хөндийд байрлаж байна";
        String url = "http://twitter.com/sukhee56/status/640834315731910656";
        Tweet tweet = new Tweet(data,data,"tst","sukhee56",null,"topsy",url,"640834315731910656","filename");

        DAO.saveEntry(tweet,"testing");
        Assert.assertFalse(DAO.isNew(tweet,"testing"));
    }

    @Test
    public void testIsNew(){
        String data = "blba bla bla ";
        String url = "http://twitter.com/sukhee56/status/640834315731910656";
        Tweet tweet = new Tweet(data,data,"tst","sukhee56",null,"topsy",url,"640834315731910656","filename");

        DAO.saveEntry(tweet,"testing");
        Assert.assertFalse(DAO.isNew(tweet,"testing"));

        data = "just some data";
        url = "http://twitter.com/sukhee56/status/640834315731910656";
        tweet = new Tweet(data,data,"tst","sukhee56",null,"topsy",url,"640834315731910656","filename");

        Assert.assertTrue(DAO.isNew(tweet, "testing"));

    }

    @Test
    public void testgetCollectionCountInLanguage(){

        Assert.assertEquals(4283, DAO.getCollectionCountInLanguage("tweets", "scraping"));

        Assert.assertEquals(0, DAO.getCollectionCountInLanguage("x", "scraping"));

        Assert.assertEquals(0, DAO.getCollectionCountInLanguage("tweets", "x"));

        Assert.assertEquals(0, DAO.getCollectionCountInLanguage("x", "x"));
    }

    @Test
    public void testBasicDBObjects(){
        Assert.assertEquals(2989, DAO.getBasicDBObjects(0, 10, "amh", "tweets", "scraping").size());

        Assert.assertEquals(0, DAO.getBasicDBObjects(0, 10, "amh", "x", "scraping").size());

        Assert.assertEquals(0, DAO.getBasicDBObjects(0, 10, "amh", "tweets", "x").size());

        Assert.assertEquals(0, DAO.getBasicDBObjects(0, 10, "amh", "x", "x").size());

        Assert.assertEquals(0, DAO.getBasicDBObjects(0, 10, "x", "tweets", "scraping").size());

        Assert.assertEquals(0, DAO.getBasicDBObjects(0, 10, "x", "x", "x").size());
    }


    @After
    public void tearDown() {
        if (mongodExecutable != null)
            mongodExecutable.stop();
    }
}
