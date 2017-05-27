package edu.columbia.main.screen_logging;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Gideon on 26/05/2017.
 */
public class TaskLogger {
    String language;
    AtomicInteger savedCount = new AtomicInteger(0);
    AtomicInteger dupsCount = new AtomicInteger(0);
    AtomicInteger notInLang = new AtomicInteger(0);

    public TaskLogger(String language) {
        this.language = language;
    }

    public void incrementSaved(){
        this.savedCount.incrementAndGet();
    }
    public void incrementDuplicate(){
        this.dupsCount.incrementAndGet();
    }
    public void incrementNotInLang(){
        this.notInLang.incrementAndGet();
    }

    public void addToSavedCount(int val){
        this.savedCount.addAndGet(val);
    }

    public void addTodupsdCount(int val){
        this.dupsCount.addAndGet(val);
    }

    public void addToNotInLang(int val){
        this.notInLang.addAndGet(val);
    }


    public int getSavedCount(){
        return this.savedCount.get();
    }

    public int getDupsCount(){
        return this.dupsCount.get();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "Language: "+ language + ": {" +
                "Saved Documents: " + savedCount +
                "  |  Not in language documents: " + notInLang +
                "  |  Additional duplicates Found: " + dupsCount +
                '}';
    }
}
