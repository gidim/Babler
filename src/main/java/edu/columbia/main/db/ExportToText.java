package edu.columbia.main.db;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Projections;
import edu.columbia.main.db.Models.BlogPost;
import edu.columbia.main.db.Models.ForumPost;
import edu.columbia.main.db.Models.Tweet;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.io.File;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Gideon on 10/3/15.
 */

/**
 * Writes all data fiels of documents in DB into a single text file!
 */
public class ExportToText {

    public static void saveAllLanguageDataToFile(String language, String file) throws IOException {

        Logger log = Logger.getLogger(ExportToText.class);

        File f = new File(file);

       MongoCollection blogs = DAO.getCollectionForClass(BlogPost.class);
       MongoCollection forums = DAO.getCollectionForClass(ForumPost.class);
       MongoCollection tweets = DAO.getCollectionForClass(Tweet.class);

       MongoCursor<BasicDBObject> it = blogs.find(eq("languageCode", language)).projection(Projections.include("data")).iterator();
        while(it.hasNext()){
            String data = it.next().getString("data");
            FileUtils.writeStringToFile(f,data + "\n","UTF-8",true);
            log.info("saved: " + data);
        }

        it = forums.find(eq("languageCode", language)).projection(Projections.include("data")).iterator();
        while(it.hasNext()){
            String data = it.next().getString("data");
            FileUtils.writeStringToFile(f,data + "\n","UTF-8",true);
            log.info("saved: " + data);
        }

        it = tweets.find(eq("languageCode", language)).projection(Projections.include("data")).iterator();
        while(it.hasNext()){
            String data = it.next().getString("data");
            FileUtils.writeStringToFile(f,data + "\n","UTF-8",true);
            log.info("saved: " + data);
        }

    }



}
