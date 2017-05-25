package edu.columbia.main.language_id.textcat;

import edu.columbia.main.Utils;
import edu.columbia.main.configuration.BabelConfig;
import edu.columbia.main.language_id.LanguageClassifier;
import edu.columbia.main.language_id.LanguageCode;
import edu.columbia.main.language_id.Result;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * TextCategorizer is able to categorize texts by computing the similarity of
 * the FingerPrint of a text with a collection of the FingerPrints of the
 * categories.
 * 
 */
public class TextCategorizer extends LanguageClassifier{



	private InputStream confFile = null;
	
	private final static int UNKNOWN_LIMIT = 1;

	private final String jarConfFile = "languageData/language_fp/textcat.conf";

	private ArrayList<FingerPrint> categories = new ArrayList<FingerPrint>();

	public TextCategorizer() {
		loadCategories();
	}

	@Override
	public Result detectLanguage(String text) throws IOException, ClassNotFoundException {
		return new Result(this.categorize(Utils.removePuntuation(text)),true,-1);
	}



	/**
	 * creates a new TextCategorizer with the given configuration file. the
	 * configuration file maps paths to FingerPrint files to categories which
	 * are used to categorize the texts passed to the TextCategorizer.
	 *
	 * @param confFile
	 *            the path to the configuration file
	 */
	public TextCategorizer(InputStream confFile) {
		setConfFile(confFile);
	}

	/**
	 * sets the configuration file path.
	 *
	 * @param confFile
	 *            the path to the configuration file
	 */
	public void setConfFile(InputStream confFile) {
		this.confFile = confFile;
		loadCategories();
	}

	/**
	 * clears the categories-collection and fills it with the FingerPrints given
	 * in the configuration file.
	 */
	private void loadCategories() {
		this.categories.clear();
		Logger log = Logger.getLogger(TextCategorizer.class);
		try {
			MyProperties properties = new MyProperties();
			if (confFile == null) {
				properties.load(TextCategorizer.class.getClassLoader()
						.getResourceAsStream(jarConfFile));
			} else {
				properties.load(new FileInputStream(confFile.toString()));
			}
			for (Entry<String, String> entry : properties.entrySet()) {
				FingerPrint fp;
				if (confFile == null) {
					fp = new FingerPrint(TextCategorizer.class.getClassLoader()
							.getResourceAsStream(entry.getKey()));
				} else {
					fp = new FingerPrint(BabelConfig.getResourcesPath() + entry.getKey());
				}
				fp.setCategory(entry.getValue());
				this.categories.add(fp);
                this.supportedLanguages.add(new LanguageCode(entry.getValue(), LanguageCode.CodeTypes.ISO_639_2));
			}
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
		}
	}

	/**
	 * categorizes the text passed to it
	 * 
	 * @param text
	 *            text to be categorized
	 * @return the category name given in the configuration file
	 */
	public String categorize(String text) {
		if(text.length() < UNKNOWN_LIMIT) {
			return "unknown";
		}
		FingerPrint fp = new FingerPrint();
		fp.create(text);
		fp.categorize(categories);

		return fp.getCategory();
	}
	
	/**
	 * categorizes only a certain amount of characters in the text. recommended
	 * when categorizing large texts in order to increase performance.
	 * 
	 * @param text text to be analysed
	 * @param limit number of characters to be analysed
	 * @return the category name given in the configuration file
	 */
	public String categorize(String text,int limit) {
		if(limit > (text.length()-1)) {
			return this.categorize(text);
		}
		return this.categorize(text.substring(0,limit));
	}
	
	/**
	 * categorizes a text but returns a map containing all categories and their
	 * distances to the text.
	 * 
	 * @param text text to be categorized
	 * @return HashMap with categories as keys and distances as values
	 */
	public Map<String,Integer> getCategoryDistances(String text) {
		if (this.categories.isEmpty()) {
			loadCategories();
		}
		FingerPrint fp = new FingerPrint();
		fp.create(text);
		fp.categorize(categories);
		return fp.getCategoryDistances();
	}


//	/**
//	 * reads from stdin til EOF is read. prints the determined category of the
//	 * input and terminates afterwards.
//	 *
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		byte[] data = new byte[1024];
//		int read;
//		String s = "";
//		if (args.length == 0 || args[0].equals("-categorize")) {
//			try {
//				while ((read = System.in.read(data)) != (-1)) {
//					s += new String(data, 0, read);
//				}
//			} catch (IOException ioe) {
//				ioe.printStackTrace();
//			}
//			TextCategorizer guesser = new TextCategorizer();
//			System.out.println(guesser.categorize(s));
//		} else if(args.length > 1 && args[0].equals("-createfp")) {
//			FingerPrint fp = new FingerPrint();
//			fp.create(new File(args[1]));
//			if(args.length > 2) {
//				fp.setCategory(args[2]);
//			} else {
//				fp.setCategory(args[1].replaceAll("\\..*",""));
//			}
//			fp.save();
//		} else {
//			System.out.println("Options:\n\n\t-categorize\tdetermines languageCode of stdin\n\t-createfp <file> <category>\tcreates fingerprint from file");
//		}
//	}
}
