/**
 * Created by Gideon on 9/23/15.
 */


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.columbia.main.db.Models.BlogPost;
import edu.columbia.main.db.MongoDB;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static com.mongodb.client.model.Filters.*;


public class TestBlogPosts {


    MongoDatabase  testDb;
    MongoCollection posts;


    @Before
    public void setUp() throws UnknownHostException {
        testDb = MongoDB.INSTANCE.getDatabase("test");
        posts = testDb.getCollection("posts",BasicDBObject.class);
    }

    @Test
    public void insertToDB() {
        String data = "Исламын бүлэглэлүүд нь Узбек, Тажик, Киргиз улсыг хамарсан Ферганын хөндийд байрлаж байна";
        String url = "http://twitter.com/sukhee56/status/640834315731910656";
        BlogPost post = new BlogPost(data,"tst",null,"source",url,"GUID231253423","");
        posts.insertOne(post);
        BasicDBObject obj = (BasicDBObject) posts.find(eq("data", data)).first();
        BlogPost post2 = new BlogPost(obj);
        assertNotNull(post.equals(post2));
    }

    @Test(expected = MongoWriteException.class)
    public void noDuplicatesAllowed() {
        String data = "this is a blog post !";
        String url = "http://www.columbia.edu";
        BlogPost post = new BlogPost(data,"tst",null,"source",url,"GUID231253423","");
        posts.insertOne(post);
        String data2 = "this is a blog post !";
        String url2 = "http://www.columbia.edu";
        BlogPost post2 = new BlogPost(data,"tst",null,"source",url,"GUID231253423","");
        posts.insertOne(post2);
    }

    @Test
    public void testQueryByLanaguage(){
        String data = "Исламын бүлэглэлүүд нь Узбек, Тажик, Киргиз улсыг хамарсан Ферганын хөндийд байрлаж байна";
        String url = "http://twitter.com/sukhee56/status/640834315731910656";
        BlogPost post = new BlogPost(data,"tst",null,"source",url,"GUID231253423","");
        posts.insertOne(post);

        data = "Исламын бүлэглэлүүд нь Узasdasdsadбек, Тажик, Киргиз улсыг хамарсан Ферганын хөндийд байрлаж байна";
        url = "http://twitter.coaasdsadm/sukhee56/status/640834315731910656";
        post = new BlogPost(data,"tst",null,"source",url,"GUID231253423","");
        posts.insertOne(post);

        data = "Исламын бүлэглэлүүд нь Узбек, asdasdТажик, Киргиз улсыг хамарсан Ферганын хөндийд байрлаж байна";
        url = "http://twitter.com/sukhee56/status/640834315731910656";
        post = new BlogPost(data,"other languageCode",null,"source",url,"GUID231253423","");
        posts.insertOne(post);


        //declare list of posts in languageCode "tst"
        ArrayList<BlogPost> results = new ArrayList<BlogPost>();
        for (Object cur : posts.find(eq("languageCode", "tst"))) {
            results.add(new BlogPost((BasicDBObject)cur));
        }

        assertTrue(results.size() == 2);


    }


    @Test(expected = NullPointerException.class)
    public void testEmptyData(){
        String data = "";
        String url = "http://twitter.com/sukhee56/status/640834315731910656";
        BlogPost post = new BlogPost(data,"tst",null,"source",url,"GUID231253423","");
        posts.insertOne(post);
        BasicDBObject obj = (BasicDBObject) posts.find(eq("data", data)).first();
        BlogPost post2 = new BlogPost(obj);
        assertNotNull(post.equals(post2));
    }

    @After
    public void tearDown(){
        posts.drop();
    }

}

