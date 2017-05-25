package edu.columbia.main;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

/**
 * Created by Gideon on 4/8/15.
 */

/**
 * A wrapper for apache multi threaded http client
 */
public class MTHttpClient {

    MultiThreadedHttpConnectionManager connectionManager;
    HttpClient client;

    public MTHttpClient(){
        MultiThreadedHttpConnectionManager connectionManager =
                new MultiThreadedHttpConnectionManager();
        client = new HttpClient(connectionManager);
    }


    public HttpClient getClient() {
        return client;
    }
}
