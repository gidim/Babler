package edu.columbia.main.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.sun.org.apache.regexp.internal.RE;
import edu.columbia.main.configuration.BabelConfig;

/**
 * A wrapper for Google's custom search engine API
 */
public class GoogleCSE {
    private Customsearch search;

    public GoogleCSE() {
        search = new Customsearch.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                .setApplicationName("Babel").build();

    }

    /**
     * Search Google CSE for query
     * @param query to search for
     * @return List of results
     * @throws IOException
     */
    public List<Result> search(String query) throws IOException {
        return search(query,100);
    }

    /**
     * Search Google CSE for query
     * @param query to search for
     * @param numOfResults of results to return
     * @return List of results
     * @throws IOException
     */
    public List<Result> search(String query, int numOfResults) throws IOException {
        List<Result> results = new ArrayList<Result>();
        Customsearch.Cse.List list = search.cse().list(query);

        // ID
        list.setKey(BabelConfig.getInstance().getConfigFromFile().google_cse_key());
        list.setCx(BabelConfig.getInstance().getConfigFromFile().google_cse_cx());

        //Exact terms
        for(long i = 1 ; i < numOfResults ; i+=10){
            list.setStart(i);
            results.addAll(list.execute().getItems());
        }

        return results;
    }



}