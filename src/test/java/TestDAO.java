import edu.columbia.main.db.DAO;
import edu.columbia.main.db.Models.Tweet;
import edu.columbia.main.db.MongoDB;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

/**
 * Created by Gideon on 10/1/15.
 */
public class TestDAO {


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
    public void tearDown(){
        MongoDB.INSTANCE.getDatabase("testing").drop();
    }

}
