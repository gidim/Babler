package edu.columbia.main.language_id.lingpipe;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gideon on 1/29/15.
 */
public class Munge {
    static Logger log = Logger.getLogger(Munge.class);
    public Munge() throws IOException {

        File inDir = new File("/Users/Gideon/Documents/dev/Babel/temp");
        File outDir = new File("/Users/Gideon/Documents/dev/Babel/temp/munged");
        String[] languageDirNames = inDir.list();
        for (int i = 0; i < languageDirNames.length; ++i) {
            if (languageDirNames[i].startsWith(".")) {
                continue;
            }
            Pattern pattern = Pattern.compile("[a-zA-Z]+");
            Matcher matcher = pattern.matcher(languageDirNames[i]);
            matcher.find();
            String language = matcher.group();
            if(language.equals("munged")){
                continue;
            }
            File langDir = new File(inDir, languageDirNames[i]);
            String charset = extractCharset(langDir,languageDirNames[i]);
            transcode(language, langDir, charset, outDir,languageDirNames[i].replace("text","sentences.txt"));

        }
    }

    static void transcode(String language, File langDir, String charset,
                          File outDir, String filePath)
            throws IOException {

        File inFile = new File(langDir,filePath);
        FileInputStream fileIn = new FileInputStream(inFile);
        InputStreamReader isReader = new InputStreamReader(fileIn,charset);
        BufferedReader bufReader = new BufferedReader(isReader);

        File langOutDir = new File(outDir,language);
        langOutDir.mkdir();
        new File(langOutDir.getPath()+"/").mkdir();
        File outFile = new File(langOutDir,language + ".txt");
        outFile.createNewFile();
        FileOutputStream fileOut = new FileOutputStream(outFile);
        OutputStreamWriter osWriter = new OutputStreamWriter(fileOut,"UTF-8");
        BufferedWriter bufWriter = new BufferedWriter(osWriter);


        log.info("\n" + language);
        log.info("reading from=" + inFile + " charset=" + charset);
        log.info("writing to=" + outFile + " charset=utf-8");

        long totalLength = 0L;
        String line;
        while ((line = bufReader.readLine()) != null) {
            if (line.length() == 0) continue;
            int index = line.indexOf("\t");
            String newline = line.substring(index+1);
            // System.out.println("line=" + line);
            // System.out.println("New line=" + newline);
            totalLength += newline.length();
            bufWriter.write(newline);
            bufWriter.write(" ");
        }
        log.info("total length=" + totalLength);

        bufWriter.close();
        bufReader.close();
    }

    static String extractCharset(File dir, String filename) throws IOException {
        if(filename.contains("fra")){
            log.info("\n");
        }
        File metaFile = new File(dir,filename.replace("text","meta.txt"));
        FileInputStream fileIn = new FileInputStream(metaFile);
        InputStreamReader isReader = new InputStreamReader(fileIn);
        BufferedReader bufReader = new BufferedReader(isReader);
        try {
            while (true) {
                String line = bufReader.readLine();
                Pattern pattern = Pattern.compile("content encoding\\s+(\\S+)$");
                Matcher matcher = pattern.matcher(line);
                if (!matcher.find()) continue;
                bufReader.close();
                return matcher.group(1);
            }
        }
        catch(Exception ex){
            return "utf-8";
        }
    }

}
