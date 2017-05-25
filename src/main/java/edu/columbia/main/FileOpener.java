package edu.columbia.main;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Gideon on 10/2/14.
 * Provides a wrapper for file opening
 */
public class FileOpener {

    public String file;
    Logger log = Logger.getLogger(FileOpener.class);

    /**
     * opens a file
     * @param path location of file
     */
    public FileOpener(String path){
        try {
            file = FileUtils.readFileToString(new File(path),"UTF-8");
        } catch (IOException e) {
            log.debug("Failed to open: "+path);
            log.error(e);
        }
    }
    @Override
    public String toString(){
        return this.file;
    }

}
