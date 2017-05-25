package edu.columbia.main.twitter;

import edu.columbia.main.configuration.ConfigFromFile;
import org.aeonbits.owner.ConfigFactory;

/**
 * Created by Gideon on 1/29/16.
 */

/**
 * A Twitter API Key
 */
public class TwitterKey
{
    private String consumer;//consumer
    private String secret; //secret
    private String access;//access
    private String token_secret;//token secret


    public TwitterKey(String consumer, String secret, String access, String token_secret)
    {
        this.consumer = consumer;
        this.secret = secret;
        this.access = access;
        this.token_secret = token_secret;
    }

    public String getConsumer() {
        return consumer;
    }

    public String getSecret() {
        return secret;
    }

    public String getAccess() {
        return access;
    }

    public String getToken_secret() {
        return token_secret;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public void setToken_secret(String token_secret) {
        this.token_secret = token_secret;
    }
}
