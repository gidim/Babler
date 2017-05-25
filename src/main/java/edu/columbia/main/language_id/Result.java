package edu.columbia.main.language_id;

/**
 * An languageCode detection test result
 */
public class Result {

    public Result(String languageCode, boolean isReliable, double confidence) {

        if(languageCode == null){
            this.languageCode = "UNKNOWN";
            isReliable = false;
            confidence = 0;
        }
        else {
            this.languageCode = languageCode;
            this.isReliable = isReliable;
            this.confidence = confidence;
        }
    }

    public String languageCode;
    public boolean isReliable;
    public double confidence;

    public String getLanguageName(){
        return com.neovisionaries.i18n.LanguageAlpha3Code.getByCode(this.languageCode).getName();
    }

    @Override
    public String toString() {
        return "Result{" +
                "languageCode='" + languageCode + '\'' +
                ", isReliable=" + isReliable +
                ", confidence=" + confidence +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result)) return false;

        Result result = (Result) o;

        return languageCode.equals(result.languageCode);

    }

    @Override
    public int hashCode() {
        return languageCode.hashCode();
    }
}
