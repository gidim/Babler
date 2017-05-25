package edu.columbia.main.language_id;

import com.neovisionaries.i18n.LanguageAlpha3Code;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by Gideon on 2/2/16.
 */

/**
 * This class represent a language code of type ISO_639_2
 */
public class LanguageCode {



    /* ISO_639_2 code */
    private String languageCode;

    /* Language full name */
    private String languageName;

    public enum CodeTypes {
        ISO_639_1, ISO_639_2
    }

    /**
     * Constructor. Automatically converts input to ISO_639_2
     * @param lang code
     * @param type ISO_639_2 or ISO_639_1
     */
    public LanguageCode(String lang, CodeTypes type) {
        Logger log = Logger.getLogger(LanguageCode.class);

        if(lang == "jw") //fix for javenese since cld2 return unofficial language code
            lang = "jav";

        if(lang == "jw") //fix for javenese since cld2 return unofficial language code
            lang = "jav";

        if (type == CodeTypes.ISO_639_2) {
            this.languageCode = lang;
            LanguageAlpha3Code code = com.neovisionaries.i18n.LanguageAlpha3Code.getByCode(lang);
            if (code != null)
                this.languageName = code.getName();
        } else {
            //convert ISO_639_1 to ISO_639_2
            LanguageAlpha3Code code = com.neovisionaries.i18n.LanguageAlpha3Code.getByCode(lang);
            if(code != null) {
                this.languageCode = code.name();
                this.languageName = code.getName();
            }

            else{
                log.error("Couldn't find ISO_639_2 code for lang: "+lang);
            }
        }

    }



    /**
     * Converts language name to language code
     * @param languageName to convert
     * @return a language code
     * @throws Exception if that language was not found
     */
    public static LanguageCode convertLanguageNameToCode(String languageName) throws Exception {
        List lst = LanguageAlpha3Code.findByName(languageName);

        if (lst == null || lst.isEmpty()) {
            throw new Exception("Cannot find code for language: " + languageName);
        }
        return new LanguageCode((LanguageAlpha3Code) lst.get(0));
    }

    /**
     * Converts a string containing an ISO_639_1 language code to ISO_639_2
     * @param iso1 code to convert
     * @return string that contains ISO_639_2 code
     */
    public static String convertIso1toIso2(String iso1) {
        LanguageAlpha3Code code = com.neovisionaries.i18n.LanguageAlpha3Code.getByCode(iso1);
        return code.name();
    }

    /**
     * Converts a string containing an ISO_639_2 language code to ISO_639_1
     * @param iso2 to convert
     * @return string that contains ISO_639_1 code
     */
    public static String convertIso2toIso1(String iso2) {
        com.neovisionaries.i18n.LanguageCode code = com.neovisionaries.i18n.LanguageCode.getByCode(iso2);
        return code.name();


    }


    public LanguageCode(LanguageAlpha3Code code) {
        this.languageCode = code.name();
    }

    public String getLanguageCode() {
        return languageCode;
    }


    @Override
    public String toString() {
        return "LanguageCode{" +
                "languageCode='" + languageCode + '\'' +
                ", languageName='" + languageName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LanguageCode)) return false;

        LanguageCode that = (LanguageCode) o;

        return languageCode.equals(that.languageCode);

    }

    @Override
    public int hashCode() {
        return languageCode.hashCode();
    }
}