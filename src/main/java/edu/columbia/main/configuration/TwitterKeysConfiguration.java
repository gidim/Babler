package edu.columbia.main.configuration;

import edu.columbia.main.twitter.TwitterKey;
import org.aeonbits.owner.ConfigFactory;

/**
 * Created by Gideon on 1/29/16.
 */


/**
 * Manages distribution of twitter keys
 */

public class TwitterKeysConfiguration {


    TwitterKey [] allKeys;

    public TwitterKeysConfiguration()
    {

        //read all keys from ConfigFromFile.java
        ConfigFromFile cfg = BabelConfig.getInstance().getConfigFromFile();

        //save keys into allKeys array
        //check if arrays are same size
        String [] consumer = cfg.twitter_consumer();
        String [] secret = cfg.twitter_secret();
        String [] access = cfg.twitter_access();
        String [] token_secret = cfg.twitter_token_secret();


        allKeys = new TwitterKey[consumer.length];
        for(int i =0; i < allKeys.length; i++) {
            allKeys[i] = new TwitterKey(consumer[i], secret[i], access[i], token_secret[i]);
        }

    }


    public TwitterKey[] getByUserKeys()
    {
        //create new TwitterKey Array in size allKeys.length / 2;
        TwitterKey[] user = new TwitterKey[allKeys.length/2];
        //fill array (loop)
        for(int i =0; i<allKeys.length/2; i++){
            user[i] = allKeys[i];
        }
        return user;
    }


    public TwitterKey[] getBySeedQueryKeys()
    {
        //create new TwitterKey Array in size allKeys.length / 2;

        //fill array (loop)
        TwitterKey[] query = new TwitterKey[(allKeys.length/2) +1];
        //fill array (loop)
        int j = 0;
        for(int i =allKeys.length/2; i<allKeys.length; i++, j++){
            query[j] = allKeys[i];
        }
        return query;

    }

}