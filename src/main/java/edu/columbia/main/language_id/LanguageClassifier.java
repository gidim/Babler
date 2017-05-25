package edu.columbia.main.language_id;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Gideon on 2/2/16.
 */

/**
 * Abstract class to represent a language classifier
 */
public abstract class LanguageClassifier {

    public ArrayList<LanguageCode> supportedLanguages = new ArrayList<>();
    abstract public Result detectLanguage(String text) throws  IOException, ClassNotFoundException;

    /**
     * Generates a list of support languages by this classifier
     * @param langs list of languages codes as string
     * @param type language code type of the entries in langs
     */
    protected void buildListOfSupportedLanguageCodes(String [] langs, LanguageCode.CodeTypes type){
        this.supportedLanguages = new ArrayList<>();
        for(String lang : langs){
            supportedLanguages.add(new LanguageCode(lang,type));
        }
    }

    /**
     * Generates a list of support languages by this classifier
     * @param langs a list of language names (not codes). For example English, Hebrew, Spanish
     */
    protected void buildListOfSupportedLanguageCodesFromLanguageNames(String [] langs){
        this.supportedLanguages = new ArrayList<>();
         Logger log = Logger.getLogger(LanguageClassifier.class);
        for(String lang : langs){
            try {
                supportedLanguages.add(LanguageCode.convertLanguageNameToCode(lang));
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    public ArrayList<LanguageCode> getSupportedLanguages() {
        return supportedLanguages;
    }

}
