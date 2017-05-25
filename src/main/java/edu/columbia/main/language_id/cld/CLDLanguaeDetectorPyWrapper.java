package edu.columbia.main.language_id.cld;

import edu.columbia.main.language_id.Result;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;


/**
 * This class is used to detect in which languageCode a text is.
 * It's a wrapper for a python script based on Google's compact languageCode detector.
 *
 */
public class CLDLanguaeDetectorPyWrapper {
    /** name of the python script file*/
    public static String detectScript = "langDetect_nltk.py";
    /** a temp text file to store the string */
    public static String tempFile = "tempFile.txt";

    static Logger log = Logger.getLogger(CLDLanguaeDetectorPyWrapper.class);


    /**
     * Detects the languageCode of a string
     * @param text String to detect
     * @return a Result object with languageCode and reliability
     * @throws Exception If can't find the python script
     */
    public static Result detect(final String text) throws Exception {

        String path = CLDLanguaeDetectorPyWrapper.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8").replace("Babel.jar!/main/main.jar","").replace("file:","");

        File script = new File(decodedPath+detectScript);
        if(!script.exists()){
            throw new Exception("Cannot find langDetect.py at: "+decodedPath+detectScript);
        }


        String lang;
        boolean isReliable;
        File tempFileHandler = new File(tempFile);

        //write string to file
        try {
            FileUtils.writeStringToFile(tempFileHandler, text, "UTF-8");
        } catch (IOException e) {
            log.error(e);
        }

        //pass to python
        ProcessBuilder pb = new ProcessBuilder("python", decodedPath+detectScript, tempFile);
        Process p = null;
        String ret = null;

        try {
            p = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            ret = in.readLine();


        } catch (IOException e) {
            log.error(e);
        }

        //read return value
        // lang: %s ,reliable: isRealiable@' % detectedLangName)
        lang = ret.substring(8, ret.indexOf(',')-1);
        String temp = ret.substring(ret.indexOf("reliable:") + "reliable:".length() + 1, ret.length());
        int scoreInt = Integer.parseInt(temp.substring(0, 1));

        String [] split = null;

        try {
            split = ret.substring(ret.indexOf("[("), ret.length()).split(",");
        }
        catch (Exception x){
            Result rest = new Result(lang, true, 100);
            log.info(rest);
            tempFileHandler.delete();
            return rest;

        }

        double score = Double.parseDouble(split[3].replaceAll("\\)","").replaceAll("\\]",""));
        if(scoreInt == 1)
            isReliable = true;
        else
            isReliable = false;

        Result rest = new Result(lang, isReliable,score);


        //delete file
        tempFileHandler.delete();


        return rest;

    }

}