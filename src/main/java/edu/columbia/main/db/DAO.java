package edu.columbia.main.db;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.columbia.main.db.Models.BlogPost;
import edu.columbia.main.db.Models.DBEntry;
import edu.columbia.main.db.Models.ForumPost;
import edu.columbia.main.db.Models.Tweet;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;

/**
 * Created by Gideon on 9/30/15.
 */

/**
 * This class manages all communication with MongoDB
 */
public class DAO {


    /**
     * Saves an entry to file
     * @param entry
     * @param dbName usually scrapig
     * @return true if success
     */
    public static boolean saveEntry(DBEntry entry, String dbName){

        if(entry == null || !entry.isValid())
            return false;

        Logger log = Logger.getLogger(DAO.class);

        MongoDatabase db = MongoDB.INSTANCE.getDatabase(dbName);

        String collectionName = getCollectionName(entry);


        MongoCollection collection = db.getCollection(collectionName,BasicDBObject.class);

        try {
            collection.insertOne(entry);
            return true;
        }
        catch (MongoWriteException ex){
            if (ex.getCode() != 11000) // Ignore errors about duplicates
                log.error(ex.getError().getMessage());
            return false;
        }

    }

    /**
     * Gets collection name based on class of entry
     * @param entry one of the optional model in Models.
     * @return name of collection
     */
    private static String getCollectionName(DBEntry entry) {
        String collectionName = "";
        if (entry instanceof Tweet)
            collectionName = "tweets";
        else if(entry instanceof BlogPost)
            collectionName = "blogPosts";
        else if(entry instanceof ForumPost)
            collectionName = "forumPosts";
        return collectionName;
    }


    /**
     * Saves an entry to scraping db
     * @param entry to save
     * @return true if success
     */
    public static boolean saveEntry(DBEntry entry){
        return saveEntry(entry,"scraping");
    }


    /**
     * checks if an entry exists in the dbName DB
     * @param entry to check
     * @param dbName to check in
     * @return true if new
     */
    public static boolean isNew(DBEntry entry, String dbName){

        if(entry == null)
            return false;

        MongoDatabase db = MongoDB.INSTANCE.getDatabase(dbName);
        String collectionName = getCollectionName(entry);

        MongoCollection collection = db.getCollection(collectionName,BasicDBObject.class);

        BasicDBObject obj = (BasicDBObject) collection.find(eq("_id", entry.getId())).first();
        if(obj != null)
            return false;
        else
            return true;
    }

    /**
     * Check if an entry exists in scraping db
     * @param entry to check
     * @return true if new
     */
    public static boolean isNew(DBEntry entry){
        return isNew(entry,"scraping");
    }


    /**
     * Returns a MongoCollection based on Model Class
     * @param type Model Class
     * @return MongoCollection for that class
     */
    public static MongoCollection getCollectionForClass(Class type) {
        MongoDatabase db = MongoDB.INSTANCE.getDatabase("scraping");
        String collectionName = "";
        if (Tweet.class == type)
            collectionName = "tweets";
        else if (BlogPost.class == type)
            collectionName = "blogPosts";
        else if (ForumPost.class == type)
            collectionName = "forumPosts";

        return db.getCollection(collectionName, BasicDBObject.class);
    }


    /**
     * Search the DB  for tweets in a specific language
      * @param language to search for
     * @return list of users with tweets in that language
     */
    public static Set<String> getUsersWithTweetInLanguage(String language) {
        MongoCollection collection = getCollectionForClass(Tweet.class);
        Set<String> s = new HashSet<String>();
        //find users with tweets having language
        MongoCursor cursor = collection.find(and(eq("languageCode", language))).projection(include("url")).iterator();
        while(cursor.hasNext())
        {
            JSONObject obj = new JSONObject(cursor.next().toString());
            String url = obj.get("url").toString();
            String [] urlParts = url.split("/");
            s.add(urlParts[3]);
        }
        return s;
    }

    /**
     * returns a list of all twitter users in db
     * @return
     */
    public static Set<String> getAllTwitterUserIds(){
        MongoCollection collection = getCollectionForClass(Tweet.class);
        Set<String> s = new HashSet<String>();
        MongoCursor cursor = collection.find().projection(include("url")).iterator();
        while(cursor.hasNext())
        {
            JSONObject obj = new JSONObject(cursor.next().toString());
            String url = obj.get("url").toString();
            String [] urlParts = url.split("/");
            s.add(urlParts[3]);
        }
        return s;

    }

    /**
     *
     * @param c class to count
     * @param source where data was collected from
     * @param language
     * @return count of all documents in db
     */
    public static long countBySourceAndLanguage(Class c, String source, String language) {
        MongoCollection collection = getCollectionForClass(c);
        return collection.count(and(eq("collectedFrom", source), eq("languageCode", language)));
    }


    /**
     * Counts blog posts in language that hasn't been optimizd by PostExtractor
     * @param lang of posts
     * @return count
     */
    public static long countNonOpimizedBlogPostsInLanguage(String lang) {
        MongoCollection collection = getCollectionForClass(BlogPost.class);
        return collection.count(and(exists("optimized", false), eq("languageCode", lang)));

    }
    /**
     * fetches blog posts in language that hasn't been optimizd by PostExtractor
     * supports pagination
     *
     * @param lang of posts
     * @param n number of posts to fetch
     * @param skip post number to start for
     * @return list of BlogPosts
     */
    public static ArrayList<BlogPost> getNNonOpimizedBlogPostsInLanguage(String lang, int n, int skip) {
        MongoCollection collection = getCollectionForClass(BlogPost.class);
        ArrayList<BlogPost> posts = new ArrayList<>();
        collection.find(and(exists("optimized", false), eq("languageCode", lang))).skip(skip).batchSize(n).into(posts);
        return posts;
    }

    /**
     * returns count of number of docs belonging to collection
     *
     * @param collectionName
     * @param dbName
     * @return count of docs in collection
     */
    public static long getCollectionCountInLanguage(String collectionName, String dbName) {
        MongoDatabase db = MongoDB.INSTANCE.getDatabase(dbName);
        MongoCollection collection = db.getCollection(collectionName, BasicDBObject.class);
        return collection.count();
    }

    /**
     * Runs query to get 'k' docs starting from position 'start'
     *
     * @param start index
     * @param k number of docs to fetch
     * @param langcode docs belonging to langcode
     * @return list of BasicDBObjects
     */
    public static ArrayList<BasicDBObject> getBasicDBObjects(int start, int k, String langcode, String collectionName, String dbName) {
        ArrayList<BasicDBObject> data = new ArrayList<BasicDBObject>();
        MongoDatabase db = MongoDB.INSTANCE.getDatabase(dbName);
        MongoCollection collection = db.getCollection(collectionName, BasicDBObject.class);
        collection.find(and(exists("optimized", false), eq("languageCode", langcode))).skip(start).batchSize(k).into(data);
        return data;
    }

    /**
     * Replace a current db entry with another
     * @param type
     * @param old entry
     * @param newPost entry
     */
    public static void replaceEntry(Class type, DBEntry old, DBEntry newPost) {
        MongoCollection collection = getCollectionForClass(type);
        collection.replaceOne(eq("_id", old.getId()), newPost);
    }

    /**
     * deletes an entry from the DB
     * @param entry to delete
     */
    public static void deleteEntry(DBEntry entry) {
        if(entry == null)
            return;

        MongoDatabase db = MongoDB.INSTANCE.getDatabase("scraping");
        String collectionName = getCollectionName(entry);

        MongoCollection collection = db.getCollection(collectionName, BasicDBObject.class);
        collection.deleteOne(eq("_id",entry.getId()));
    }

    /**
     * Checks if a document exists in the DB by url
     * @param c type of document
     * @param url to check for
     * @return true if not exists
     */
    public static boolean isNewByURL(Class c, String url) {

        MongoCollection collection = getCollectionForClass(c);
        BasicDBObject obj = (BasicDBObject) collection.find(eq("url",url)).first();
        if(obj != null)
            return false;
        else
            return true;
    }
}
