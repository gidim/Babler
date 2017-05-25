package edu.columbia.main;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.params.ClientPNames;
import org.apache.log4j.Logger;
import sun.management.AgentConfigurationError;

import javax.swing.text.html.HTML;
import java.io.IOException;


/**
 * Wrapper for Apache HTTP Client
 * Created by Gideon on 9/19/14.
 */


public class HTTPClient {

    Logger log = Logger.getLogger(HTTPClient.class);

    /** a string to hold the data received in the response */
    private String HTMLData;

    /**
     * generates a apache.commons.httpclient and makes the request to the url
     * @param url url to fetch
     */
    public HTTPClient(String url) {

        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();

        AgentConfigurationError httpclient;
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);

        // Create a method instance.
        GetMethod method = new GetMethod(url);

        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                log.error("Method failed: " + method.getStatusLine());
            }

            // Read the response body.
            byte[] responseBody = method.getResponseBody();

            // Deal with the response.
            // Use caution: ensure correct character encoding and is not binary data
            //System.out.println("Downloads HTML: " + new String(responseBody));

            HTMLData = new String(responseBody);

        } catch (HttpException e) {
            log.debug("Fatal protocol violation: " + e.getMessage());
            log.error(e);
        } catch (IOException e) {
            log.debug("Fatal transport error: " + e.getMessage());
            log.error(e);
        } finally {
            // Release the connection.
            method.releaseConnection();
        }

    }

    /**
     *
     * @return the html data saved by the constructur
     */
    public String getHTMLData() {
        return HTMLData;
    }


}
