
package edu.columbia.main.twitter;

public abstract class TwitterScraperTemplate{

    protected Logger log;
    protected Twitter twitter;
    protected String lang;
    protected LanguageDetector languageDetector;

    public abstract void scrapeByLanguage();
    public abstract void searchAndSave(String word, LanguageDetector lp, String lang);
    
    public void setKey(Twitter key){
        this.twitter = key
    }

}