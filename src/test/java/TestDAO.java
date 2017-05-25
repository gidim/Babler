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

    @After
    public void tearDown(){
        MongoDB.INSTANCE.getDatabase("testing").drop();
    }

}
