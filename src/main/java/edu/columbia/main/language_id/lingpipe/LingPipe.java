package edu.columbia.main.language_id.lingpipe;

import com.aliasi.classify.*;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Strings;
import edu.columbia.main.language_id.LanguageClassifier;
import edu.columbia.main.language_id.LanguageCode;
import edu.columbia.main.language_id.Result;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Gideon on 1/30/15.
 */

/**
 * Public interface for LingPipe classifier
 */
public class LingPipe extends LanguageClassifier{


    public static final double RELIABLE_THRESH = 0.85;
    public BaseClassifier<CharSequence> classifier;
    static Logger log = Logger.getLogger(LingPipe.class);


    public LingPipe(String pathToFile){

        //load model from file
        try {
            this.classifier = (BaseClassifier<CharSequence>) AbstractExternalizable.readResourceObject("/"+pathToFile);
        } catch (IOException e) {
            log.error(e);
        } catch (ClassNotFoundException e) {
            log.debug("Class not found");
            log.error(e);
        }

        //build list of languageCode codes
        String [] lpLanguages = {"afr","als","ara","azj","bak","bcl","bel","ben","bos","bre","bul","cat","ceb","ces","cmn","cym","dan","deu","ell","eng","epo","est","eus","fao","fas","fin","fra","glg","gom","gsw","guj","hat","heb","hif","hin","hrv","hun","hye","ido","ind","isl","ita","jav","jpn","kal","kat","kaz","kir","kor","kur","lat","lav","lim","lit","ltz","lus","mal","mkd","mlg","mlt","mon","mri","msa","nep","new","nld","nno","nob","nso","oci","pam","pol","por","pus","ron","sah","sin","slk","slv","sna","spa","srp","swa","swe","swh","tam","tat","tel","tgk","tgl","tha","tok","tur","ukr","urd","uzb","vie","vol","xho","zho","zul"};
        buildListOfSupportedLanguageCodes(lpLanguages, LanguageCode.CodeTypes.ISO_639_2);
    }



    @Override
    public  Result detectLanguage(String text) throws IOException, ClassNotFoundException {

        JointClassification classification = (JointClassification) this.classifier.classify(text);
        String bestLang = classification.bestCategory();
        double probability = classification.conditionalProbability(0);
        boolean isReliable = false;
        if(probability >= RELIABLE_THRESH){
            isReliable = true;
        }
        return new Result(bestLang,isReliable,probability);
    }




    private void normalizedToLingPipe(String directory) throws IOException {

        File dir = new File(directory);

        for(File f : dir.listFiles()){
            log.info(f.getName());

            if(f.isDirectory()){
                continue;
            }

            File newFile = new File(directory+"/cleaned/"+f.getName()+"E");

            BufferedReader br = new BufferedReader(new FileReader(f));
            FileOutputStream fos = new FileOutputStream(newFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("<s> ", "").replaceAll(" </s>",".");
                writer.write(line);
            }
            writer.close();
            br.close();

        }


    }

    private void trainSubset(String datDir) throws IOException, ClassNotFoundException {

        //smallest size
        long cebSize  = getSizeOfCorpura(datDir,"ceb");
        int trainSize = (int) (0.75 * cebSize); //%75
        int testSize = (int) (cebSize-trainSize);// %25


        File dataDir = new File(datDir);
        String[] categories = dataDir.list();
        HashMap<String,Integer> trainByteLocation = new HashMap<String, Integer>();

        for(int j =0 ; j < 50 ; j ++) {
            //start the trainer
            DynamicLMClassifier classifier
                    = DynamicLMClassifier
                    .createNGramProcess(categories, 5); //5 ngrams


            for (int i = 0; i < categories.length; ++i) {

                //load category data
                String category = categories[i];
                File trainingFile = new File(new File(dataDir, category),
                        category + ".txt");
                FileInputStream fileIn
                        = new FileInputStream(trainingFile);
                InputStreamReader reader
                        = new InputStreamReader(fileIn, Strings.UTF8);


                //determine offset
                //find a number between 0 and langsize-telugusize
                long langSize = getSizeOfCorpura(datDir, category);
                int readFromByte = randInt(0, safeLongToInt(langSize - cebSize));


                //read data from corups to buffer
                char[] csBuf = new char[trainSize];
                //skip to the random spot
                reader.skip(readFromByte);
                reader.read(csBuf);
                String text = new String(csBuf, 0, trainSize);

                //save the location from where we read
                trainByteLocation.put(category,readFromByte+trainSize);

                Classification c = new Classification(category);
                Classified<CharSequence> classified
                        = new Classified<CharSequence>(text, c);
                classifier.handle(classified);
                reader.close();
            }
            //compile trainer
            File modelFile = new File("model"+j);
            AbstractExternalizable.compileTo(classifier, modelFile);

            //start the evaluator
            BaseClassifier<CharSequence> evalClassifier
                    = (BaseClassifier<CharSequence>) AbstractExternalizable.readObject(modelFile);
            BaseClassifierEvaluator<CharSequence> evaluator
                    = new BaseClassifierEvaluator<CharSequence>(evalClassifier,categories,false);

            //evaulate


            char[] csBuf = new char[testSize / 1000];
            for (int i = 0; i < categories.length; ++i) {
                String category = categories[i];
                File trainingFile = new File(new File(dataDir,category),
                        category + ".txt");
                FileInputStream fileIn
                        = new FileInputStream(trainingFile);
                InputStreamReader reader
                        = new InputStreamReader(fileIn,Strings.UTF8);

                reader.skip(trainByteLocation.get(category)); // skip training data

                for (int k = 0; k < 1000; ++k) {
                    reader.read(csBuf);
                    String text = new String(csBuf);
                    //System.out.println(text);
                    Classification c = new Classification(category);
                    Classified<CharSequence> cl
                            = new Classified<CharSequence>(text,c);
                    evaluator.handle(cl);
                }
                reader.close();
            }
            log.info("-------TEST "+j + " -----------");
            log.info(evaluator.toString());


        }



    }

    private long getSizeOfCorpura(String dataDir, String lang) throws IOException {

        String path  = dataDir+"/"+lang+"/"+lang+".txt";
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), Strings.UTF8));
        int charCount = 0;
        char[] cbuf = new char[1024];
        int read = 0;
        while((read = reader.read(cbuf)) > -1) {
            charCount += read;
        }
        reader.close();
        return charCount;
    }


    public static void train(String datDir, String modFile, int nGram) throws IOException {

        int trainSize = 300000;
        File dataDir = new File(datDir);
        File modelFile = new File(modFile);

        String[] categories = dataDir.list();

        DynamicLMClassifier classifier
                = DynamicLMClassifier
                .createNGramProcess(categories,nGram);

        for (int i = 0; i < categories.length; ++i) {

            String category = categories[i];
            log.info("Training " + category);
            File trainingFile = new File(new File(dataDir,category),
                    category + ".txt");

            int numChars = numberOfCharsInFile(trainingFile, null);
            if(numChars > trainSize /* && !isOurLanguage(category )*/){
                numChars = trainSize;
            }
            /*
            else {
                numChars -= 50 * 1000;
            }
            */

            if(numChars < trainSize){
                log.info("Error with " + category);
                continue;
            }

            FileInputStream fileIn
                    = new FileInputStream(trainingFile);
            InputStreamReader reader
                    = new InputStreamReader(fileIn, Strings.UTF8);
            char[] csBuf = new char[numChars];
            reader.read(csBuf);
            String text = new String(csBuf,0,numChars);
            Classification c = new Classification(category);
            Classified<CharSequence> classified
                    = new Classified<CharSequence>(text,c);
            classifier.handle(classified);
            reader.close();
        }
        AbstractExternalizable.compileTo(classifier, modelFile);
    }

    private static boolean isOurLanguage(String category) {

        if(category.equals("ceb") || category.equals("lit") || category.equals("tel") || category.equals("kur") || category.equals("tok") || category.equals("kaz") )
            return true;

        else
            return false;

    }


    public static void evaluate(String datDir, String modFile, int testSize, int numChars, int numTests) throws IOException, ClassNotFoundException {
        File dataDir = new File(datDir);
        File modelFile = new File(modFile);

        String[] categories = dataDir.list();

        BaseClassifier<CharSequence> classifier
                = (BaseClassifier<CharSequence>) AbstractExternalizable.readObject(modelFile);
        BaseClassifierEvaluator<CharSequence> evaluator
                = new BaseClassifierEvaluator<CharSequence>(classifier,categories,false);

        char[] csBuf = new char[testSize];
        for (int i = 0; i < categories.length; ++i) {
            String category = categories[i];
            if(category.equals("gle")){
                continue;
            }
            File trainingFile = new File(new File(dataDir,category),
                    category + ".txt");
            FileInputStream fileIn
                    = new FileInputStream(trainingFile);
            InputStreamReader reader
                    = new InputStreamReader(fileIn,Strings.UTF8);

            /*
            if(isOurLanguage(category)){
                numChars = numberOfCharsInFile(trainingFile,Strings.UTF8) - (50 * 1000);
            }
            */

            reader.skip(numChars); // skip training data

            for (int k = 0; k < numTests; ++k) {
                reader.read(csBuf);
                Classification c = new Classification(category);
                Classified<CharSequence> cl
                        = new Classified<CharSequence>(new String(csBuf),c);
                evaluator.handle(cl);
            }

            reader.close();
        }
        log.info(evaluator.toString());
    }

    static int numberOfCharsInFile(File f, String charsetName) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), Strings.UTF8));
        int charCount = 0;
        char[] cbuf = new char[1024];
        int read = 0;
        while((read = reader.read(cbuf)) > -1) {
            charCount += read;
        }
        reader.close();
        return charCount;
    }

    public  JointClassification detectLanguage2(String text) throws IOException, ClassNotFoundException {

        File modelFile = new File("BabelModel.gm");

        JointClassification classification = (JointClassification) this.classifier.classify(text);

        String bestLang = classification.bestCategory();
        double probability = classification.conditionalProbability(0);
        boolean isReliable = false;
        if(probability >= RELIABLE_THRESH){
            isReliable = true;
        }

        //return new Result(bestLang,isReliable,probability);
        return classification;
    }


    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    private void trainAll(String datDir) throws IOException, ClassNotFoundException {


        File dataDir = new File(datDir);
        String[] categories = dataDir.list();
        HashMap<String,Integer> trainByteLocation = new HashMap<String, Integer>();
        long cebSize  = getSizeOfCorpura(datDir,"ceb");
        File modelFile = new File("fullModel");


        //start the trainer
        DynamicLMClassifier classifier
                = DynamicLMClassifier
                .createNGramProcess(categories, 5); //5 ngrams

        //train on the entire data set
        for (int i = 0; i < categories.length; ++i) {

            //load category data
            String category = categories[i];
            File trainingFile = new File(new File(dataDir, category),
                    category + ".txt");
            FileInputStream fileIn
                    = new FileInputStream(trainingFile);
            InputStreamReader reader
                    = new InputStreamReader(fileIn, Strings.UTF8);


            //determine offset
            long langSize = getSizeOfCorpura(datDir, category);
            int trainSize = (int)(safeLongToInt(langSize) - (0.5 * cebSize));
            trainByteLocation.put(category,trainSize);


            //read data from corups to buffer
            char[] csBuf = new char[trainSize];
            reader.read(csBuf);
            String text = new String(csBuf, 0,trainSize);
            //System.out.println(text.substring(100));

            Classification c = new Classification(category);
            Classified<CharSequence> classified
                    = new Classified<CharSequence>(text, c);
            classifier.handle(classified);
            reader.close();
        }
        //compile trainer
        AbstractExternalizable.compileTo(classifier, modelFile);



        //start the evaluator
        BaseClassifier<CharSequence> evalClassifier
                = (BaseClassifier<CharSequence>) AbstractExternalizable.readObject(modelFile);
        BaseClassifierEvaluator<CharSequence> evaluator
                = new BaseClassifierEvaluator<CharSequence>(evalClassifier,categories,false);

        //evaulate


        for (int i = 0; i < categories.length; ++i) {
            String category = categories[i];
            File trainingFile = new File(new File(dataDir,category),
                    category + ".txt");
            FileInputStream fileIn
                    = new FileInputStream(trainingFile);
            InputStreamReader reader
                    = new InputStreamReader(fileIn,Strings.UTF8);

            long langSize = getSizeOfCorpura(datDir, category);
            int trainSize = (int)(safeLongToInt(langSize) - (0.5 * cebSize));

            reader.skip(trainSize); // skip training data
            int evalSize  = (int)(cebSize * 0.5);

            char[] csBuf = new char[(evalSize/1000)-100];
            for (int k = 0; k < 1000; ++k) {
                int r = reader.read(csBuf);
                String text = new String(csBuf);
                log.info("Testing text: "+text);
                Classification c = new Classification(category);
                Classified<CharSequence> cl
                        = new Classified<CharSequence>(text,c);
                evaluator.handle(cl);
            }
            reader.close();
        }
        log.info("-------FINAL TEST " + " -----------");
        log.info(evaluator.toString());


    }


}
