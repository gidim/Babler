package edu.columbia.main.article_extraction;

/**
 * Created by Gideon on 10/14/15.
 */
public class DiffbotArticle {

    private String resolved_url;
    private String title;
    private String text;
    private String humanLanguage;

    public String getResolved_url() {
        return resolved_url;
    }

    public void setResolved_url(String resolved_url) {
        this.resolved_url = resolved_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHumanLanguage() {
        return humanLanguage;
    }

    public void setHumanLanguage(String humanLanguage) {
        this.humanLanguage = humanLanguage;
    }

    @Override
    public String toString() {
        return "DiffbotArticle{" +
                "resolved_url='" + resolved_url + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", humanLanguage='" + humanLanguage + '\'' +
                '}';
    }
}
