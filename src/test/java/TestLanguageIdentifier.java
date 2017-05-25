import edu.columbia.main.language_id.LanguageCode;
import edu.columbia.main.language_id.LanguageDetector;
import edu.columbia.main.language_id.Result;
import edu.columbia.main.language_id.cld.Cld2;
import edu.columbia.main.language_id.lingpipe.LingPipe;
import edu.columbia.main.language_id.textcat.TextCategorizer;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ExactComparisonCriteria;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;


/**
 * Created by Gideon on 10/1/15.
 */
public class TestLanguageIdentifier {

    LanguageDetector ld;
    LingPipe lingPipe;
    Cld2 cld2;
    TextCategorizer tc;
    boolean local = false;

    @Before
    public void setup() throws IOException, ClassNotFoundException {
        ld = new LanguageDetector();
        lingPipe = new LingPipe("completeModel3.gm");
        tc = new TextCategorizer();

        //If running locally clean all CLD errors
        try {
            cld2 = new Cld2();
        }
        catch (Exception ex){
            local = true;
        }
        catch (Error e){
            local = true;
        }

    }




    @Test
    public void testLanguageCodeConversion(){

        String [] from1 = {"aa","kw","gd","mh"};
        String [] to2 = {"aar","cor","gla","mah"};

        for(int i = 0 ; i < from1.length ; i++){
            String from = from1[i];
            String to = to2[i];

            LanguageCode code = new LanguageCode(from, LanguageCode.CodeTypes.ISO_639_1);
            org.junit.Assert.assertTrue(code.getLanguageCode().equals(to));
        }


    }


    @Test
    public  void testLingPipe() throws IOException, ClassNotFoundException {
        String english = "This is a string in english";
        Result res = lingPipe.detectLanguage(english);
        Assert.assertTrue(res.languageCode.equals("eng"));

        String pashto = "د افغانستان د دوو تکړه ورزش کارانو ډاکټر نادرشاه او د هغه زوی پیروز خان سره د لېنز پروډکشن مرکه";
        res = lingPipe.detectLanguage(pashto);
        Assert.assertTrue(res.languageCode.equals("pus"));


    }

    @Test
    public  void testTextCat() throws IOException, ClassNotFoundException {
        String luo = "e kaka otimre e polo ka Nyasaye";
        Result res = tc.detectLanguage(luo);
        Assert.assertTrue(res.languageCode.equals("luo"));

        String amh = "የሳውዲ ፖሊስ ለሃጃጅ ያለው ክብር ይህን ያህል ነው።";
        res = tc.detectLanguage(amh);
        Assert.assertTrue(res.languageCode.equals("amh"));

    }

    @Test
    public  void testIdentifiersAgreement() throws IOException, ClassNotFoundException {
        String dutch = "Ik ben sinds 1 maand Nederlands aan het leren.";
        Result lp = lingPipe.detectLanguage(dutch);
        Result tc = this.tc.detectLanguage(dutch);
        Result cld = null;

        Assert.assertTrue(lp.languageCode.equals(tc.languageCode));

        if(local !=true){
            cld = this.cld2.detectLanguage(dutch);
            Assert.assertTrue(lp.languageCode.equals(cld.languageCode));
        }
    }

    @Test
    public  void testMajorityVote() throws Exception {
        String english = "This is a sentence in english, all classifiers should agree";
        String code = LanguageCode.convertLanguageNameToCode("English").getLanguageCode();
        Result res = ld.detectMajorityVote(english,code);
        org.junit.Assert.assertTrue(res.languageCode.equals(code));
    }

    @Test
    public  void testHierarchyCLD() throws Exception {
        if(local)
            return;

        String english = "This is a sentence in english, CLD should respond";
        String code = LanguageCode.convertLanguageNameToCode("English").getLanguageCode();
        Result res = ld.detectHierarchy(english,code);
        org.junit.Assert.assertTrue(res.languageCode.equals(code));
    }
    @Test
    public  void testHierarchyLP() throws Exception {

        String mon = "сая дээд шууд байхгүй нийгмийн гэдгийг орны байхад";
        Result res = ld.detectHierarchy(mon,"mon"); //cld doesn't support mongolian thus LingPipe should respond
        org.junit.Assert.assertTrue(res.languageCode.equals("mon"));
    }
    @Test
    public  void testHierarchyTC() throws Exception {
        String luo = "bende donge ing'eyo kanyo ng'ato abiro nyaka nikech nitie obiro ang'o";
        Result res = ld.detectHierarchy(luo,"luo");
        org.junit.Assert.assertTrue(res.languageCode.equals("luo"));
    }




}
