
package edu.columbia.main;

public class TwitterScraperFactory{

    public TwitterScraperTemplate getTwitterScraper(String type){
        if(type == "TwitterSentiment"){
            return new TwitterSentimentScraper();
        } else if(type == "TwitterCodeSwitch"){
            return new TwitterCodeSwitchScraper();
        } else if(type == "TwitterUsers"){ 
            return new TwitterUsersScraper();
        }else{
            return null;
        }
    }

}