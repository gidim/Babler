package edu.columbia.main.collection;

import edu.columbia.main.LanguageDataManager;

/**
 * Created by Gideon on 4/8/15.
 */
public class BabelScraper {


    public static final int NUM_OF_CONSUMERS = 6; //todo:change
    public static final int NUM_OF_PRODUCERS = LanguageDataManager.getLanguages().length;
    boolean users = false;

    public BabelScraper(){}
    public BabelScraper(boolean users) {
        this.users = users;
    }


}


