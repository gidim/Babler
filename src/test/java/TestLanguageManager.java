import edu.columbia.main.LanguageDataManager;
import edu.columbia.main.configuration.BabelConfig;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.fail;


/**
 * Created by Sania on 3/7/16.
 */
public class TestLanguageManager
{

    @Test
    public void testGetMostCommonWords() throws IOException {
        //from existing data for ngram=1
        Assert.assertNotNull(BabelConfig.getResource("languageData/" + "mon" + "MostCommon.txt"));
        ArrayList<String> mon = LanguageDataManager.getMostCommonWords("mon", 100, 1);
        Assert.assertNotNull(mon);
        Assert.assertEquals("нь", mon.get(0));
        Assert.assertNotNull(BabelConfig.getResource("languageData/" + "mon" + "MostCommon.txt"));
        File monMostCommon = new File(BabelConfig.getResource("languageData/" + "mon" + "MostCommon.txt"));
        Assert.assertTrue(monMostCommon.exists());

        //no most common words file - create
        Assert.assertNull(BabelConfig.getResource("languageData/" + "mih" + "MostCommon.txt"));
        ArrayList<String> mih = LanguageDataManager.getMostCommonWords("mih", 100, 1);
        Assert.assertNotNull(mih);
        Assert.assertEquals("ra", mih.get(0));
        Assert.assertNotNull(BabelConfig.getResource("languageData/" + "mih" + "MostCommon.txt"));
        File mihMostCommon = new File(BabelConfig.getResource("languageData/" + "mih" + "MostCommon.txt"));
        Assert.assertTrue(mihMostCommon.exists());
        mihMostCommon.delete();
    }




}
