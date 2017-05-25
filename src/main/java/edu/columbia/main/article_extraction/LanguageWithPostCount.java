package edu.columbia.main.article_extraction;

/**
 * Created by Gideon on 10/14/15.
 */
public class LanguageWithPostCount {


    String language;
    long count;

    public LanguageWithPostCount(String language, long count) {
        this.language = language;
        this.count = count;
    }

    @Override
    public String toString() {
        return "LanguageWithPostCount{" +
                "languageCode='" + language + '\'' +
                ", count=" + count +
                '}';
    }
}
