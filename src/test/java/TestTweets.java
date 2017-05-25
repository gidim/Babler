/**
 * Created by Gideon on 9/23/15.
 */
import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.columbia.main.db.Models.Tweet;
import edu.columbia.main.db.MongoDB;
import junit.framework.Assert;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static com.mongodb.client.model.Filters.*;


public class TestTweets {


        MongoDatabase  testDb;
        MongoCollection tweets;
        @Before
        public void setUp() throws UnknownHostException {
            testDb = MongoDB.INSTANCE.getDatabase("test");
            tweets = testDb.getCollection("tweets",BasicDBObject.class);

        }

        @Test
        public void insertTweetToDataBase() {
            String data = "Исламын бүлэглэлүүд нь Узбек, Тажик, Киргиз улсыг хамарсан Ферганын хөндийд байрлаж байна";
            String url = "http://twitter.com/sukhee56/status/640834315731910656";
            Tweet tweet = new Tweet(data,data,"tst","sukhee56",null,"topsy",url,"640834315731910656","filename");
            tweets.insertOne(tweet);
            BasicDBObject obj = (BasicDBObject) tweets.find(eq("data", data)).first();
            Tweet tweet2 = new Tweet(obj);
            assertNotNull(tweet.equals(tweet2));
        }

        @Test(expected = MongoWriteException.class)
        public void noDuplicatesAllowed() {
                String data = "this is a tweet !";
                String url = "http://www.columbia.edu";
                Tweet tweet = new Tweet(data,data,"tst","test",null,"topsy",url,"123456","filename");
                tweets.insertOne(tweet);
                String data2 = "this is a tweet !";
                String url2 = "http://www.columbia.edu";
                Tweet tweet2 = new Tweet(data2,data,"tst","test",null,"topsy",url2,"123456","filename");
                tweets.insertOne(tweet2);
            }
        @Test
        public void deleteFromDB() {
            String data = "test blablabla";
            String url = "http://twitter.com/sukhee56/status/640834315731910656";
            Tweet tweet = new Tweet(data,data,"tst","sukhee56",null,"topsy",url,"640834315731910656","filename");

            //add and check that it was added
            tweets.insertOne(tweet);
            BasicDBObject obj = (BasicDBObject) tweets.find(eq("data", data)).first();
            Tweet tweet2 = new Tweet(obj);
            assertNotNull(tweet.equals(tweet2));

            //delete
            obj = (BasicDBObject) tweets.find(eq("data", data)).first();
            tweets.deleteOne(eq("data", data));

            //try to fetch again
            obj = (BasicDBObject) tweets.find(eq("data", data)).first();
            assertNull(obj);



        }

        @After
        public void tearDown(){
            tweets.drop();
        }

    }

