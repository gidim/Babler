package edu.columbia.main.language_id;
import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.regexp.internal.RE;
import edu.columbia.main.configuration.BabelConfig;
import edu.columbia.main.language_id.cld.Cld2;
import edu.columbia.main.language_id.lingpipe.LingPipe;
import edu.columbia.main.Utils;
import edu.columbia.main.language_id.textcat.TextCategorizer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Created by Gideon on 9/4/15.
 */


/**
 * Language detection API that manages all supported classifiers
 */
public class LanguageDetector {

    /* LingPipe classifier */
    LingPipe lp = new LingPipe("completeModel3.gm");
    /* CLD2 Classifier */
    Cld2 cld = null;

    /* TextCat Classifier */
    edu.columbia.main.language_id.textcat.TextCategorizer tc = null;
    private ArrayList<String> lpLangs = new ArrayList<String>();
    Logger log = Logger.getLogger(LanguageDetector.class);


    /* Constructor */
    public LanguageDetector(){
        tc = new edu.columbia.main.language_id.textcat.TextCategorizer();
        try {
            cld = new Cld2();
        }
        catch (IOException e)
        {
            log.error(e);
        }
        catch (ClassNotFoundException e)
        {
            log.error(e);
        }
        catch (Error e){
            log.info("failed to load CLD. Continuing using LP language classifier. ");
        }

    }

    /**
     * Preforms language detection using majority vote approach over all classifiers
     * If CLD2 is not installed than fallback to standard language detection detectLanguage()
     * @param text to preform language detection on
     * @param lang what language is this text should be in? Used to pick classifiers
     * @return Classification results
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Result detectMajorityVote(String text, String lang) throws IOException, ClassNotFoundException {

        if(cld == null){ //if we can't load CLD no point in doing majority vote
            return detectLanguage(text,lang);
        }

        ArrayList<Result> results = new ArrayList<>();

        LanguageCode code = new LanguageCode(lang, LanguageCode.CodeTypes.ISO_639_2);
        if(lp.getSupportedLanguages().contains(code))
            results.add(lp.detectLanguage(text));
        if(tc.getSupportedLanguages().contains(code))
            results.add(tc.detectLanguage(text));
        if(cld.getSupportedLanguages().contains(code))
            results.add(cld.detectLanguage(text));

        Result res = mostCommon(results);

        if(res == null)
            return new Result(null,false,0);
        else
            return res;

    }

    /**
     * Preforms language detection with the best available classifier (based on measured accuracy)
     * @param text to preform language detection on
     * @param lang what language is this text should be in? Used to pick classifiers
     * @return Classification results
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Result detectHierarchy(String text, String lang) throws IOException, ClassNotFoundException {
        if(text == null)
            return null;

        LanguageCode code = new LanguageCode(lang, LanguageCode.CodeTypes.ISO_639_2);


        if(cld != null && cld.getSupportedLanguages().contains(code)) {
            return cld.detectLanguage(text);
        }
        else if (lp.getSupportedLanguages().contains(code)){
            return lp.detectLanguage(text);
        }
        else if(tc.getSupportedLanguages().contains(code)){
            return tc.detectLanguage(text);
        }
        else{
            log.info("Language: " + lang + " not supported!");
        }

        return null;

    }

    /**
     * Main entry point for identification. Will choose approach based on configuration provided in config file
     * @param text to preform language detection on
     * @param lang what language is this text should be in? Used to pick classifiers
     * @return Classification results
     */
    public Result detectLanguage(String text, String lang) {
        try {
            if (BabelConfig.getInstance().getConfigFromFile().useMajorityVote() && cld!=null)
                return detectMajorityVote(text, lang);
            else
                return detectHierarchy(text, lang);

            }
        catch(NullPointerException np){
            log.error(np);
            return null;
        }
        catch (Exception e) {
            log.error("Can't run language id - > Shutting down!");
            log.error(e);
            System.exit(0);
        }
        return null;
    }

    @Deprecated
    public Result detectOld(String text, String lang){
        LanguageCode code = new LanguageCode(lang, LanguageCode.CodeTypes.ISO_639_2);

        if(lang == null || lp.getSupportedLanguages().contains(code)) {
            try {
                return lp.detectLanguage(text);
            } catch (IOException e) {
                log.error(e);
            } catch (ClassNotFoundException e) {
                log.error(e);
            }
        }

        else{
            return new Result(tc.categorize(Utils.removePuntuation(text)),true,2);
        }

        return null;
    }


    private static <T> T mostCommon(List<T> list) {

        if(list == null || list.size() == 0)
            return null;
        if(list.size() == 1)
            return list.get(0);

        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }
        if(map.size() == list.size()){  //if sizes are the same it means that all the values in list are unique
            return null;
        }
        return max.getKey();
    }
}